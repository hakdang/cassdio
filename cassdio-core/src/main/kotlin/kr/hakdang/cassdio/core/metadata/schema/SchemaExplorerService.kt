package kr.hakdang.cassdio.core.metadata.schema

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.Row
import kr.hakdang.cassdio.core.exception.ConflictException
import kr.hakdang.cassdio.core.exception.NotFoundException
import kr.hakdang.cassdio.core.metadata.cluster.ManagedClusterRepository
import kr.hakdang.cassdio.core.metadata.cluster.ManagedClusterSessionManager
import kr.hakdang.cassdio.core.metadata.config.ManagedClusterEnvironment
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Instant
import java.util.Base64
import java.util.UUID

@Service
class SchemaExplorerService(
    private val clusterRepository: ManagedClusterRepository,
    private val sessionManager: ManagedClusterSessionManager,
    private val catalogRepository: SchemaCatalogRepository,
    private val historyRepository: SchemaChangeHistoryRepository,
    private val clock: Clock = Clock.systemUTC(),
) {
    fun listKeyspaces(
        clusterId: UUID,
        includeSystem: Boolean = false,
        refresh: Boolean = false,
    ): List<SchemaKeyspace> {
        ensureCluster(clusterId)
        if (refresh) sessionManager.clear(clusterId)
        val session = sessionManager.getSession(clusterId)
        val tableCounts = countByKeyspace(session, "system_schema.tables")
        val viewCounts = countByKeyspace(session, "system_schema.views")
        val typeCounts = countByKeyspace(session, "system_schema.types")

        return session
            .execute("SELECT keyspace_name, durable_writes, replication FROM system_schema.keyspaces")
            .map { row ->
                val name = row.requireString("keyspace_name")
                SchemaKeyspace(
                    name = name,
                    durableWrites = row.getBoolean("durable_writes"),
                    replication = row.stringMap("replication"),
                    system = name.isSystemKeyspace(),
                    queryable = !name.isSystemKeyspace(),
                    tableCount = (tableCounts[name] ?: 0) + (viewCounts[name] ?: 0),
                    userTypeCount = typeCounts[name] ?: 0,
                    describeCql = keyspaceCql(name, row.getBoolean("durable_writes"), row.stringMap("replication")),
                )
            }.filter { includeSystem || !it.system }
            .sortedWith(compareBy<SchemaKeyspace> { it.system }.thenBy { it.name })
            .toList()
    }

    fun keyspaceDetail(
        clusterId: UUID,
        keyspaceName: String,
    ): SchemaKeyspace =
        listKeyspaces(clusterId, includeSystem = true).firstOrNull { it.name == keyspaceName }
            ?: throw NotFoundException("Keyspace not found.")

    fun listTables(
        clusterId: UUID,
        keyspaceName: String,
        cursor: String? = null,
        limit: Int = 50,
    ): SchemaTablePage {
        ensureCluster(clusterId)
        val session = sessionManager.getSession(clusterId)
        val catalogs = catalogRepository.listTableCatalog(clusterId, keyspaceName)
        val offset = cursor?.decodeCursor() ?: 0
        val pageLimit = limit.coerceIn(1, 200)
        val tables =
            session
                .execute(
                    """
                    SELECT keyspace_name, table_name, comment FROM system_schema.tables
                    WHERE keyspace_name = ${keyspaceName.cqlLiteral()}
                    """.trimIndent(),
                ).map {
                    SchemaTableSummary(
                        keyspaceName = keyspaceName,
                        name = it.requireString("table_name"),
                        kind = SchemaTableKind.TABLE,
                        comment = it.getString("comment"),
                        catalog = catalogs[it.requireString("table_name")],
                    )
                }.toList()
        val views =
            session
                .execute(
                    """
                    SELECT keyspace_name, view_name, base_table_name FROM system_schema.views
                    WHERE keyspace_name = ${keyspaceName.cqlLiteral()}
                    """.trimIndent(),
                ).map {
                    SchemaTableSummary(
                        keyspaceName = keyspaceName,
                        name = it.requireString("view_name"),
                        kind = SchemaTableKind.MATERIALIZED_VIEW,
                        comment = "Materialized view on ${it.getString("base_table_name").orEmpty()}",
                        catalog = catalogs[it.requireString("view_name")],
                    )
                }.toList()

        val sorted = (tables + views).sortedWith(compareBy<SchemaTableSummary> { it.kind.name }.thenBy { it.name })
        val items = sorted.drop(offset).take(pageLimit)
        val nextOffset = offset + items.size
        return SchemaTablePage(
            keyspaceName = keyspaceName,
            items = items,
            nextCursor = nextOffset.takeIf { it < sorted.size }?.encodeCursor(),
        )
    }

    fun tableDetail(
        clusterId: UUID,
        keyspaceName: String,
        tableName: String,
    ): SchemaTableDetail {
        ensureCluster(clusterId)
        val session = sessionManager.getSession(clusterId)
        val tableRow =
            session
                .execute(
                    """
                    SELECT * FROM system_schema.tables
                    WHERE keyspace_name = ${keyspaceName.cqlLiteral()} AND table_name = ${tableName.cqlLiteral()}
                    """.trimIndent(),
                ).one()
        val viewRow =
            tableRow ?: session
                .execute(
                    """
                    SELECT * FROM system_schema.views
                    WHERE keyspace_name = ${keyspaceName.cqlLiteral()} AND view_name = ${tableName.cqlLiteral()}
                    """.trimIndent(),
                ).one()
        if (viewRow == null) throw NotFoundException("Table not found.")

        val columns = listColumns(clusterId, keyspaceName, tableName)
        val indexes = listIndexes(session, keyspaceName, tableName)
        val views = listViews(session, keyspaceName, tableName)
        val options = tableOptions(viewRow)
        val kind = if (tableRow == null) SchemaTableKind.MATERIALIZED_VIEW else SchemaTableKind.TABLE
        val statement = createTableCql(keyspaceName, tableName, columns, options, kind)

        return SchemaTableDetail(
            keyspaceName = keyspaceName,
            name = tableName,
            kind = kind,
            columns = columns,
            indexes = indexes,
            views = views,
            options = options,
            createStatement = statement,
            catalog = catalogRepository.findTableCatalog(clusterId, keyspaceName, tableName),
        )
    }

    fun listColumns(
        clusterId: UUID,
        keyspaceName: String,
        tableName: String,
    ): List<SchemaColumn> {
        ensureCluster(clusterId)
        val session = sessionManager.getSession(clusterId)
        val catalogs = catalogRepository.listColumnCatalog(clusterId, keyspaceName, tableName)
        return session
            .execute(
                """
                SELECT column_name, type, kind, position, clustering_order FROM system_schema.columns
                WHERE keyspace_name = ${keyspaceName.cqlLiteral()} AND table_name = ${tableName.cqlLiteral()}
                """.trimIndent(),
            ).map { row ->
                val name = row.requireString("column_name")
                val type = row.requireString("type")
                SchemaColumn(
                    name = name,
                    type = type,
                    kind = row.columnKind(),
                    position = row.getInt("position"),
                    clusteringOrder = row.getString("clustering_order")?.takeIf { it != "none" },
                    catalog = catalogs[name],
                    userTypeLink = type.userTypeLink(keyspaceName),
                )
            }.sortedWith(compareBy<SchemaColumn> { it.kind.sortOrder() }.thenBy { it.position }.thenBy { it.name })
            .toList()
    }

    fun listUserTypes(
        clusterId: UUID,
        keyspaceName: String,
    ): List<SchemaUserTypeSummary> {
        ensureCluster(clusterId)
        val session = sessionManager.getSession(clusterId)
        return session
            .execute(
                """
                SELECT type_name, field_names FROM system_schema.types
                WHERE keyspace_name = ${keyspaceName.cqlLiteral()}
                """.trimIndent(),
            ).map {
                SchemaUserTypeSummary(
                    keyspaceName = keyspaceName,
                    name = it.requireString("type_name"),
                    fieldCount = it.stringList("field_names").size,
                )
            }.sortedBy { it.name }
            .toList()
    }

    fun userTypeDetail(
        clusterId: UUID,
        keyspaceName: String,
        typeName: String,
    ): SchemaUserTypeDetail {
        ensureCluster(clusterId)
        val session = sessionManager.getSession(clusterId)
        val row =
            session
                .execute(
                    """
                    SELECT type_name, field_names, field_types FROM system_schema.types
                    WHERE keyspace_name = ${keyspaceName.cqlLiteral()} AND type_name = ${typeName.cqlLiteral()}
                    """.trimIndent(),
                ).one() ?: throw NotFoundException("UDT type not found.")
        val fields =
            row.stringList("field_names").zip(row.stringList("field_types")).mapIndexed { index, pair ->
                SchemaUserTypeField(name = pair.first, type = pair.second, position = index)
            }
        val fieldCql = fields.joinToString(",\n  ") { "${it.name.cqlIdentifier()} ${it.type}" }
        return SchemaUserTypeDetail(
            keyspaceName = keyspaceName,
            name = typeName,
            fields = fields,
            createStatement = "CREATE TYPE ${keyspaceName.cqlIdentifier()}.${typeName.cqlIdentifier()} (\n  $fieldCql\n);",
        )
    }

    fun updateTableCatalog(
        clusterId: UUID,
        keyspaceName: String,
        tableName: String,
        request: TableCatalogUpdate,
        actor: String = "System",
    ): TableCatalogMetadata {
        tableDetail(clusterId, keyspaceName, tableName)
        val now = Instant.now(clock)
        val metadata =
            TableCatalogMetadata(
                clusterId = clusterId,
                keyspaceName = keyspaceName,
                tableName = tableName,
                owner = request.owner,
                escalationContact = request.escalationContact,
                description = request.description,
                dataFreshness = request.dataFreshness,
                retentionPolicy = request.retentionPolicy,
                accessPattern = request.accessPattern,
                tags = request.tags,
                updatedAt = now,
            )
        catalogRepository.saveTableCatalog(metadata)
        historyRepository.record(
            SchemaChangeRecord(
                changeId = UUID.randomUUID(),
                clusterId = clusterId,
                keyspaceName = keyspaceName,
                tableName = tableName,
                changeType = SchemaChangeType.CATALOG_UPDATED,
                actor = actor,
                beforeCql = null,
                afterCql = null,
                diff = listOf("Table catalog metadata updated."),
                details = mapOf("owner" to request.owner.orEmpty(), "tags" to request.tags.joinToString(",")),
                createdAt = now,
            ),
        )
        return metadata
    }

    fun updateColumnCatalog(
        clusterId: UUID,
        keyspaceName: String,
        tableName: String,
        columnName: String,
        request: ColumnCatalogUpdate,
        actor: String = "System",
    ): ColumnCatalogMetadata {
        val column =
            listColumns(clusterId, keyspaceName, tableName).firstOrNull { it.name == columnName }
                ?: throw NotFoundException("Column not found.")
        val now = Instant.now(clock)
        val metadata =
            ColumnCatalogMetadata(
                clusterId = clusterId,
                keyspaceName = keyspaceName,
                tableName = tableName,
                columnName = column.name,
                description = request.description,
                sensitive = request.sensitive,
                maskingPolicy = request.maskingPolicy,
                exportPolicy = request.exportPolicy,
                tags = request.tags,
                updatedAt = now,
            )
        catalogRepository.saveColumnCatalog(metadata)
        historyRepository.record(
            SchemaChangeRecord(
                changeId = UUID.randomUUID(),
                clusterId = clusterId,
                keyspaceName = keyspaceName,
                tableName = tableName,
                changeType = SchemaChangeType.CATALOG_UPDATED,
                actor = actor,
                beforeCql = null,
                afterCql = null,
                diff = listOf("Column catalog metadata updated for $columnName."),
                details = mapOf("column" to columnName, "sensitive" to request.sensitive.toString()),
                createdAt = now,
            ),
        )
        return metadata
    }

    fun history(
        clusterId: UUID,
        keyspaceName: String,
        tableName: String?,
    ): List<SchemaChangeRecord> {
        ensureCluster(clusterId)
        return historyRepository.list(clusterId, keyspaceName, tableName)
    }

    fun dropKeyspace(
        clusterId: UUID,
        keyspaceName: String,
        request: SchemaDangerousActionRequest,
    ): SchemaDangerousActionResult {
        val cluster = ensureCluster(clusterId)
        requireConfirmation(request.confirmation, "DROP KEYSPACE $keyspaceName")
        if (cluster.environment == ManagedClusterEnvironment.PROD) {
            return workflowRequired("Production keyspace drop requires workflow approval.")
        }
        val before = keyspaceDetail(clusterId, keyspaceName).describeCql
        sessionManager.getSession(clusterId).execute("DROP KEYSPACE ${keyspaceName.cqlIdentifier()}")
        val change = recordDangerousChange(clusterId, keyspaceName, null, SchemaChangeType.DROP_KEYSPACE, request.actor, before, null)
        return SchemaDangerousActionResult(true, false, "Keyspace dropped.", change)
    }

    fun dropTable(
        clusterId: UUID,
        keyspaceName: String,
        tableName: String,
        request: SchemaDangerousActionRequest,
    ): SchemaDangerousActionResult {
        val cluster = ensureCluster(clusterId)
        requireConfirmation(request.confirmation, "DROP TABLE $keyspaceName.$tableName")
        if (cluster.environment == ManagedClusterEnvironment.PROD) {
            return workflowRequired("Production table drop requires workflow approval.")
        }
        val before = tableDetail(clusterId, keyspaceName, tableName).createStatement
        sessionManager.getSession(clusterId).execute("DROP TABLE ${keyspaceName.cqlIdentifier()}.${tableName.cqlIdentifier()}")
        val change = recordDangerousChange(clusterId, keyspaceName, tableName, SchemaChangeType.DROP_TABLE, request.actor, before, null)
        return SchemaDangerousActionResult(true, false, "Table dropped.", change)
    }

    fun truncateTable(
        clusterId: UUID,
        keyspaceName: String,
        tableName: String,
        request: SchemaDangerousActionRequest,
    ): SchemaDangerousActionResult {
        val cluster = ensureCluster(clusterId)
        requireConfirmation(request.confirmation, "TRUNCATE $keyspaceName.$tableName")
        if (cluster.environment == ManagedClusterEnvironment.PROD) {
            return workflowRequired("Production truncate requires workflow approval.")
        }
        val before = tableDetail(clusterId, keyspaceName, tableName).createStatement
        sessionManager.getSession(clusterId).execute("TRUNCATE ${keyspaceName.cqlIdentifier()}.${tableName.cqlIdentifier()}")
        val change =
            recordDangerousChange(clusterId, keyspaceName, tableName, SchemaChangeType.TRUNCATE_TABLE, request.actor, before, before)
        return SchemaDangerousActionResult(true, false, "Table truncated.", change)
    }

    private fun ensureCluster(clusterId: UUID) = clusterRepository.findById(clusterId) ?: throw NotFoundException("Cluster not found.")

    private fun workflowRequired(message: String) = SchemaDangerousActionResult(false, true, message, null)

    private fun recordDangerousChange(
        clusterId: UUID,
        keyspaceName: String,
        tableName: String?,
        type: SchemaChangeType,
        actor: String,
        before: String?,
        after: String?,
    ): SchemaChangeRecord =
        historyRepository.record(
            SchemaChangeRecord(
                changeId = UUID.randomUUID(),
                clusterId = clusterId,
                keyspaceName = keyspaceName,
                tableName = tableName,
                changeType = type,
                actor = actor,
                beforeCql = before,
                afterCql = after,
                diff = simpleDiff(before, after),
                details = mapOf("guard" to "confirmation"),
                createdAt = Instant.now(clock),
            ),
        )

    private fun requireConfirmation(
        actual: String,
        expected: String,
    ) {
        if (actual != expected) {
            throw ConflictException("Confirmation must exactly match: $expected")
        }
    }

    private fun countByKeyspace(
        session: CqlSession,
        table: String,
    ): Map<String, Int> =
        session
            .execute("SELECT keyspace_name FROM $table")
            .map { it.requireString("keyspace_name") }
            .groupingBy { it }
            .eachCount()

    private fun listIndexes(
        session: CqlSession,
        keyspaceName: String,
        tableName: String,
    ): List<SchemaIndex> =
        session
            .execute(
                """
                SELECT index_name, table_name, kind, options FROM system_schema.indexes
                WHERE keyspace_name = ${keyspaceName.cqlLiteral()} AND table_name = ${tableName.cqlLiteral()}
                """.trimIndent(),
            ).map {
                SchemaIndex(
                    name = it.requireString("index_name"),
                    tableName = it.requireString("table_name"),
                    kind = it.getString("kind"),
                    target = it.stringMap("options")["target"],
                    options = it.stringMap("options"),
                )
            }.sortedBy { it.name }
            .toList()

    private fun listViews(
        session: CqlSession,
        keyspaceName: String,
        tableName: String,
    ): List<SchemaView> =
        session
            .execute(
                """
                SELECT view_name, base_table_name, where_clause, include_all_columns FROM system_schema.views
                WHERE keyspace_name = ${keyspaceName.cqlLiteral()}
                """.trimIndent(),
            ).map {
                SchemaView(
                    name = it.requireString("view_name"),
                    baseTableName = it.requireString("base_table_name"),
                    whereClause = it.getString("where_clause"),
                    includeAllColumns = it.getBoolean("include_all_columns"),
                )
            }.filter { it.baseTableName == tableName }
            .sortedBy { it.name }
            .toList()

    private fun tableOptions(row: Row): Map<String, String> =
        listOf("comment", "compaction", "compression", "caching", "default_time_to_live", "gc_grace_seconds")
            .mapNotNull { name -> row.getObject(name)?.toString()?.let { name to it } }
            .toMap()

    private fun createTableCql(
        keyspaceName: String,
        tableName: String,
        columns: List<SchemaColumn>,
        options: Map<String, String>,
        kind: SchemaTableKind,
    ): String {
        val columnLines =
            columns.joinToString(",\n  ") {
                val staticSuffix = if (it.kind == SchemaColumnKind.STATIC) " static" else ""
                "${it.name.cqlIdentifier()} ${it.type}$staticSuffix"
            }
        val partitionKeys =
            columns
                .filter { it.kind == SchemaColumnKind.PARTITION_KEY }
                .sortedBy { it.position }
                .map { it.name.cqlIdentifier() }
        val clusteringKeys =
            columns
                .filter { it.kind == SchemaColumnKind.CLUSTERING }
                .sortedBy { it.position }
                .map { it.name.cqlIdentifier() }
        val partition = if (partitionKeys.size == 1) partitionKeys.single() else partitionKeys.joinToString(", ", "(", ")")
        val primary = (listOf(partition) + clusteringKeys).joinToString(", ")
        val optionCql = options["comment"]?.takeIf { it.isNotBlank() }?.let { "\nWITH comment = ${it.cqlLiteral()}" }.orEmpty()
        val statement = if (kind == SchemaTableKind.TABLE) "CREATE TABLE" else "CREATE MATERIALIZED VIEW"
        val qualifiedName = "${keyspaceName.cqlIdentifier()}.${tableName.cqlIdentifier()}"
        return "$statement $qualifiedName (\n  $columnLines,\n  PRIMARY KEY ($primary)\n)$optionCql;"
    }

    private fun keyspaceCql(
        keyspaceName: String,
        durableWrites: Boolean,
        replication: Map<String, String>,
    ): String =
        "CREATE KEYSPACE ${keyspaceName.cqlIdentifier()} WITH replication = ${replication.cqlMapLiteral()} AND durable_writes = $durableWrites;"
}

private fun Row.requireString(name: String): String = requireNotNull(getString(name)) { "Missing column $name." }

private fun Row.stringMap(name: String): Map<String, String> = getMap(name, String::class.java, String::class.java).orEmpty()

private fun Row.stringList(name: String): List<String> = getList(name, String::class.java).orEmpty()

private fun Row.columnKind(): SchemaColumnKind =
    when (getString("kind")) {
        "partition_key" -> SchemaColumnKind.PARTITION_KEY
        "clustering" -> SchemaColumnKind.CLUSTERING
        "static" -> SchemaColumnKind.STATIC
        else -> SchemaColumnKind.REGULAR
    }

private fun SchemaColumnKind.sortOrder(): Int =
    when (this) {
        SchemaColumnKind.PARTITION_KEY -> 0
        SchemaColumnKind.CLUSTERING -> 1
        SchemaColumnKind.STATIC -> 2
        SchemaColumnKind.REGULAR -> 3
    }

private fun String.isSystemKeyspace(): Boolean = this == "system" || startsWith("system_")

private fun String.userTypeLink(defaultKeyspace: String): SchemaUserTypeLink? {
    val cleaned = removePrefix("frozen<").removeSuffix(">")
    val primitivePrefixes =
        listOf(
            "ascii",
            "bigint",
            "blob",
            "boolean",
            "counter",
            "date",
            "decimal",
            "double",
            "duration",
            "float",
            "inet",
            "int",
            "smallint",
            "text",
            "time",
            "timestamp",
            "timeuuid",
            "tinyint",
            "uuid",
            "varchar",
            "varint",
            "list<",
            "map<",
            "set<",
            "tuple<",
        )
    if (primitivePrefixes.any { cleaned.startsWith(it) }) return null
    val parts = cleaned.split('.')
    return if (parts.size == 2) SchemaUserTypeLink(parts[0], parts[1]) else SchemaUserTypeLink(defaultKeyspace, cleaned)
}

private fun String.cqlIdentifier(): String = if (matches(Regex("[a-z][a-z0-9_]*"))) this else "\"${replace("\"", "\"\"")}\""

private fun Int.encodeCursor(): String = Base64.getUrlEncoder().withoutPadding().encodeToString(toString().toByteArray())

private fun String.decodeCursor(): Int = runCatching { String(Base64.getUrlDecoder().decode(this)).toInt() }.getOrDefault(0)

private fun simpleDiff(
    before: String?,
    after: String?,
): List<String> {
    if (before == after) return emptyList()
    return listOfNotNull(
        before?.let {
            "- ${it.lineSequence().firstOrNull().orEmpty()}"
        },
        after?.let { "+ ${it.lineSequence().firstOrNull().orEmpty()}" },
    )
}
