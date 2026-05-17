package kr.hakdang.cassdio.core.metadata.bootstrap

import kr.hakdang.cassdio.core.metadata.config.MetadataDbConfigProvider
import kr.hakdang.cassdio.core.metadata.cql.CqlExecutor
import org.springframework.stereotype.Service

@Service
class MetadataSchemaDefinitions(
    private val configProvider: MetadataDbConfigProvider,
    private val cqlExecutor: CqlExecutor,
) {
    fun ensureBootstrapTables() {
        val keyspace = configProvider.getConfig().keyspace

        listOf(
            """
            CREATE TABLE IF NOT EXISTS $keyspace.schema_migrations (
              version TEXT PRIMARY KEY,
              description TEXT,
              executed_at TIMESTAMP,
              execution_time INT,
              success BOOLEAN
            )
            """.trimIndent(),
            """
            CREATE TABLE IF NOT EXISTS $keyspace.bootstrap_locks (
              name TEXT PRIMARY KEY,
              owner TEXT,
              status TEXT,
              heartbeat_at TIMESTAMP,
              expires_at TIMESTAMP
            )
            """.trimIndent(),
            """
            CREATE TABLE IF NOT EXISTS $keyspace.installation_state (
              id TEXT PRIMARY KEY,
              installation_id UUID,
              bootstrap_completed_at TIMESTAMP,
              schema_version TEXT,
              cassdio_version TEXT,
              updated_at TIMESTAMP
            )
            """.trimIndent(),
            """
            CREATE TABLE IF NOT EXISTS $keyspace.seed_history (
              idempotency_key TEXT PRIMARY KEY,
              description TEXT,
              executed_at TIMESTAMP,
              success BOOLEAN
            )
            """.trimIndent(),
        ).forEach(cqlExecutor::execute)
    }
}
