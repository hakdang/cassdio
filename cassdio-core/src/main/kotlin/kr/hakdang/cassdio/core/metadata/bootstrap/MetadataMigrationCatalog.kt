package kr.hakdang.cassdio.core.metadata.bootstrap

import kr.hakdang.cassdio.core.metadata.config.MetadataDbConfigProvider
import org.springframework.stereotype.Component

@Component
class MetadataMigrationCatalog(
    private val configProvider: MetadataDbConfigProvider,
) {
    fun migrations(): List<MetadataMigration> {
        val keyspace = configProvider.getConfig().keyspace

        return listOf(
            MetadataMigration(
                version = "202602010001",
                description = "Create metadata bootstrap schema",
                statements =
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
                          initial_settings MAP<TEXT, TEXT>,
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
                    ),
            ),
        )
    }
}
