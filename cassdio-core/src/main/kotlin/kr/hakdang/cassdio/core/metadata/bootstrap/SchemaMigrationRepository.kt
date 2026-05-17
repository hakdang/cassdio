package kr.hakdang.cassdio.core.metadata.bootstrap

import kr.hakdang.cassdio.core.metadata.config.MetadataDbConfigProvider
import kr.hakdang.cassdio.core.metadata.cql.CqlExecutor
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class SchemaMigrationRepository(
    private val configProvider: MetadataDbConfigProvider,
    private val cqlExecutor: CqlExecutor,
) {
    fun isApplied(version: String): Boolean {
        val keyspace = configProvider.getConfig().keyspace
        val row =
            cqlExecutor.queryOne(
                "SELECT success FROM $keyspace.schema_migrations WHERE version = ${version.cqlLiteral()}",
            )

        return row?.boolean("success") == true
    }

    fun record(
        migration: MetadataMigration,
        executedAt: Instant,
        executionTimeMillis: Int,
        success: Boolean,
    ) {
        val keyspace = configProvider.getConfig().keyspace
        cqlExecutor.execute(
            """
            INSERT INTO $keyspace.schema_migrations
            (version, description, executed_at, execution_time, success)
            VALUES (
              ${migration.version.cqlLiteral()},
              ${migration.description.cqlLiteral()},
              ${executedAt.timestampLiteral()},
              $executionTimeMillis,
              ${success.cqlLiteral()}
            )
            """.trimIndent(),
        )
    }

    fun currentVersion(): String? {
        val keyspace = configProvider.getConfig().keyspace
        val row =
            cqlExecutor.queryOne(
                "SELECT version FROM $keyspace.schema_migrations WHERE success = true LIMIT 1",
            )

        return row?.string("version")
    }
}
