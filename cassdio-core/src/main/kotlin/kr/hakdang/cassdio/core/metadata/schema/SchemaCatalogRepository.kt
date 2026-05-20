package kr.hakdang.cassdio.core.metadata.schema

import kr.hakdang.cassdio.core.metadata.config.MetadataDbConfigProvider
import kr.hakdang.cassdio.core.metadata.cql.CqlExecutor
import kr.hakdang.cassdio.core.metadata.cql.CqlRow
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID

@Repository
class SchemaCatalogRepository(
    private val configProvider: MetadataDbConfigProvider,
    private val cqlExecutor: CqlExecutor,
) {
    fun findTableCatalog(
        clusterId: UUID,
        keyspaceName: String,
        tableName: String,
    ): TableCatalogMetadata? {
        val keyspace = configProvider.getConfig().keyspace
        return cqlExecutor
            .queryOne(
                """
                SELECT * FROM $keyspace.schema_table_catalog
                WHERE cluster_id = $clusterId
                  AND keyspace_name = ${keyspaceName.cqlLiteral()}
                  AND table_name = ${tableName.cqlLiteral()}
                """.trimIndent(),
            )?.toTableCatalog()
    }

    fun listTableCatalog(
        clusterId: UUID,
        keyspaceName: String,
    ): Map<String, TableCatalogMetadata> {
        val keyspace = configProvider.getConfig().keyspace
        return cqlExecutor
            .query(
                """
                SELECT * FROM $keyspace.schema_table_catalog
                WHERE cluster_id = $clusterId AND keyspace_name = ${keyspaceName.cqlLiteral()}
                """.trimIndent(),
            ).map { it.toTableCatalog() }
            .associateBy { it.tableName }
    }

    fun saveTableCatalog(
        metadata: TableCatalogMetadata,
    ) {
        val keyspace = configProvider.getConfig().keyspace
        cqlExecutor.execute(
            """
            INSERT INTO $keyspace.schema_table_catalog
            (cluster_id, keyspace_name, table_name, owner, escalation_contact, description, data_freshness, retention_policy, access_pattern, tags, updated_at)
            VALUES (
              ${metadata.clusterId},
              ${metadata.keyspaceName.cqlLiteral()},
              ${metadata.tableName.cqlLiteral()},
              ${metadata.owner.cqlNullableLiteral()},
              ${metadata.escalationContact.cqlNullableLiteral()},
              ${metadata.description.cqlNullableLiteral()},
              ${metadata.dataFreshness.cqlNullableLiteral()},
              ${metadata.retentionPolicy.cqlNullableLiteral()},
              ${metadata.accessPattern.cqlNullableLiteral()},
              ${metadata.tags.cqlSetLiteral()},
              ${metadata.updatedAt.timestampLiteral()}
            )
            """.trimIndent(),
        )
    }

    fun findColumnCatalog(
        clusterId: UUID,
        keyspaceName: String,
        tableName: String,
        columnName: String,
    ): ColumnCatalogMetadata? {
        val keyspace = configProvider.getConfig().keyspace
        return cqlExecutor
            .queryOne(
                """
                SELECT * FROM $keyspace.schema_column_catalog
                WHERE cluster_id = $clusterId
                  AND keyspace_name = ${keyspaceName.cqlLiteral()}
                  AND table_name = ${tableName.cqlLiteral()}
                  AND column_name = ${columnName.cqlLiteral()}
                """.trimIndent(),
            )?.toColumnCatalog()
    }

    fun listColumnCatalog(
        clusterId: UUID,
        keyspaceName: String,
        tableName: String,
    ): Map<String, ColumnCatalogMetadata> {
        val keyspace = configProvider.getConfig().keyspace
        return cqlExecutor
            .query(
                """
                SELECT * FROM $keyspace.schema_column_catalog
                WHERE cluster_id = $clusterId
                  AND keyspace_name = ${keyspaceName.cqlLiteral()}
                  AND table_name = ${tableName.cqlLiteral()}
                """.trimIndent(),
            ).map { it.toColumnCatalog() }
            .associateBy { it.columnName }
    }

    fun saveColumnCatalog(metadata: ColumnCatalogMetadata) {
        val keyspace = configProvider.getConfig().keyspace
        cqlExecutor.execute(
            """
            INSERT INTO $keyspace.schema_column_catalog
            (cluster_id, keyspace_name, table_name, column_name, description, sensitive, masking_policy, export_policy, tags, updated_at)
            VALUES (
              ${metadata.clusterId},
              ${metadata.keyspaceName.cqlLiteral()},
              ${metadata.tableName.cqlLiteral()},
              ${metadata.columnName.cqlLiteral()},
              ${metadata.description.cqlNullableLiteral()},
              ${metadata.sensitive},
              ${metadata.maskingPolicy.cqlNullableLiteral()},
              ${metadata.exportPolicy.cqlNullableLiteral()},
              ${metadata.tags.cqlSetLiteral()},
              ${metadata.updatedAt.timestampLiteral()}
            )
            """.trimIndent(),
        )
    }
}

private fun CqlRow.toTableCatalog(): TableCatalogMetadata =
    TableCatalogMetadata(
        clusterId = requireNotNull(uuid("cluster_id")),
        keyspaceName = requireNotNull(string("keyspace_name")),
        tableName = requireNotNull(string("table_name")),
        owner = string("owner"),
        escalationContact = string("escalation_contact"),
        description = string("description"),
        dataFreshness = string("data_freshness"),
        retentionPolicy = string("retention_policy"),
        accessPattern = string("access_pattern"),
        tags = stringSet("tags") ?: stringList("tags")?.toSet() ?: emptySet(),
        updatedAt = instant("updated_at") ?: Instant.EPOCH,
    )

private fun CqlRow.toColumnCatalog(): ColumnCatalogMetadata =
    ColumnCatalogMetadata(
        clusterId = requireNotNull(uuid("cluster_id")),
        keyspaceName = requireNotNull(string("keyspace_name")),
        tableName = requireNotNull(string("table_name")),
        columnName = requireNotNull(string("column_name")),
        description = string("description"),
        sensitive = boolean("sensitive") ?: false,
        maskingPolicy = string("masking_policy"),
        exportPolicy = string("export_policy"),
        tags = stringSet("tags") ?: stringList("tags")?.toSet() ?: emptySet(),
        updatedAt = instant("updated_at") ?: Instant.EPOCH,
    )

internal fun String.cqlLiteral(): String = "'${replace("'", "''")}'"

internal fun String?.cqlNullableLiteral(): String = this?.takeIf { it.isNotBlank() }?.cqlLiteral() ?: "null"

internal fun Iterable<String>.cqlSetLiteral(): String =
    joinToString(separator = ", ", prefix = "{", postfix = "}") { it.cqlLiteral() }

internal fun Map<String, String>.cqlMapLiteral(): String =
    entries.joinToString(separator = ", ", prefix = "{", postfix = "}") { (key, value) ->
        "${key.cqlLiteral()}: ${value.cqlLiteral()}"
    }

internal fun Instant.timestampLiteral(): String = toString().cqlLiteral()
