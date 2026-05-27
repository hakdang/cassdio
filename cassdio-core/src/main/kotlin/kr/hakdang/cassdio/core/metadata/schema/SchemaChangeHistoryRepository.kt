package kr.hakdang.cassdio.core.metadata.schema

import kr.hakdang.cassdio.core.metadata.config.MetadataDbConfigProvider
import kr.hakdang.cassdio.core.metadata.cql.CqlExecutor
import kr.hakdang.cassdio.core.metadata.cql.CqlRow
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID

@Repository
class SchemaChangeHistoryRepository(
    private val configProvider: MetadataDbConfigProvider,
    private val cqlExecutor: CqlExecutor,
) {
    fun record(change: SchemaChangeRecord): SchemaChangeRecord {
        val keyspace = configProvider.getConfig().keyspace
        cqlExecutor.execute(
            """
            INSERT INTO $keyspace.schema_change_history
            (cluster_id, keyspace_name, table_name, change_id, change_type, actor, before_cql, after_cql, diff, details, created_at)
            VALUES (
              ${change.clusterId},
              ${change.keyspaceName.cqlLiteral()},
              ${change.tableName.cqlNullableLiteral()},
              ${change.changeId},
              ${change.changeType.name.cqlLiteral()},
              ${change.actor.cqlLiteral()},
              ${change.beforeCql.cqlNullableLiteral()},
              ${change.afterCql.cqlNullableLiteral()},
              ${change.diff.cqlListLiteral()},
              ${change.details.cqlMapLiteral()},
              ${change.createdAt.timestampLiteral()}
            )
            """.trimIndent(),
        )
        return change
    }

    fun list(
        clusterId: UUID,
        keyspaceName: String,
        tableName: String?,
    ): List<SchemaChangeRecord> {
        val keyspace = configProvider.getConfig().keyspace
        val tablePredicate = tableName?.let { " AND table_name = ${it.cqlLiteral()} ALLOW FILTERING" } ?: ""
        return cqlExecutor
            .query(
                """
                SELECT * FROM $keyspace.schema_change_history
                WHERE cluster_id = $clusterId AND keyspace_name = ${keyspaceName.cqlLiteral()}$tablePredicate
                """.trimIndent(),
            ).map { it.toSchemaChangeRecord() }
            .sortedByDescending { it.createdAt }
    }
}

private fun CqlRow.toSchemaChangeRecord(): SchemaChangeRecord =
    SchemaChangeRecord(
        changeId = requireNotNull(uuid("change_id")),
        clusterId = requireNotNull(uuid("cluster_id")),
        keyspaceName = requireNotNull(string("keyspace_name")),
        tableName = string("table_name"),
        changeType = SchemaChangeType.valueOf(string("change_type") ?: SchemaChangeType.CATALOG_UPDATED.name),
        actor = string("actor") ?: "System",
        beforeCql = string("before_cql"),
        afterCql = string("after_cql"),
        diff = stringList("diff").orEmpty(),
        details = stringMap("details").orEmpty(),
        createdAt = instant("created_at") ?: Instant.EPOCH,
    )

private fun Iterable<String>.cqlListLiteral(): String = joinToString(separator = ", ", prefix = "[", postfix = "]") { it.cqlLiteral() }
