package kr.hakdang.cassdio.core.identity

import kr.hakdang.cassdio.core.metadata.config.MetadataDbConfigProvider
import kr.hakdang.cassdio.core.metadata.cql.CqlExecutor
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Instant
import java.util.UUID

@Service
class AuditService(
    private val configProvider: MetadataDbConfigProvider,
    private val cqlExecutor: CqlExecutor,
    private val clock: Clock = Clock.systemUTC(),
) {
    fun record(
        eventType: String,
        actor: String,
        targetType: String,
        targetId: String,
        details: Map<String, String> = emptyMap(),
        createdAt: Instant = Instant.now(clock),
    ) {
        val keyspace = configProvider.getConfig().keyspace
        cqlExecutor.execute(
            """
            INSERT INTO $keyspace.audit_logs
            (event_id, event_type, actor, target_type, target_id, details, created_at)
            VALUES (
              ${UUID.randomUUID()},
              ${eventType.cqlLiteral()},
              ${actor.cqlLiteral()},
              ${targetType.cqlLiteral()},
              ${targetId.cqlLiteral()},
              ${details.cqlMapLiteral()},
              ${createdAt.timestampLiteral()}
            )
            """.trimIndent(),
        )
    }
}

private fun Map<String, String>.cqlMapLiteral(): String =
    entries.joinToString(separator = ", ", prefix = "{", postfix = "}") { (key, value) ->
        "${key.cqlLiteral()}: ${value.cqlLiteral()}"
    }
