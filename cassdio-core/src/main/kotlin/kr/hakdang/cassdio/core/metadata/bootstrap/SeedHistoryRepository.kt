package kr.hakdang.cassdio.core.metadata.bootstrap

import kr.hakdang.cassdio.core.metadata.config.MetadataDbConfigProvider
import kr.hakdang.cassdio.core.metadata.cql.CqlExecutor
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class SeedHistoryRepository(
    private val configProvider: MetadataDbConfigProvider,
    private val cqlExecutor: CqlExecutor,
) {
    fun isApplied(idempotencyKey: String): Boolean {
        val keyspace = configProvider.getConfig().keyspace
        val row =
            cqlExecutor.queryOne(
                "SELECT success FROM $keyspace.seed_history " +
                    "WHERE idempotency_key = ${idempotencyKey.cqlLiteral()}",
            )

        return row?.boolean("success") == true
    }

    fun record(seed: SeedDefinition) {
        val keyspace = configProvider.getConfig().keyspace
        cqlExecutor.execute(
            """
            INSERT INTO $keyspace.seed_history
            (idempotency_key, description, executed_at, success)
            VALUES (
              ${seed.idempotencyKey.cqlLiteral()},
              ${seed.description.cqlLiteral()},
              ${Instant.now().timestampLiteral()},
              true
            )
            """.trimIndent(),
        )
    }
}
