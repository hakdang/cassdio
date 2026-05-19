package kr.hakdang.cassdio.core.metadata.bootstrap

import kr.hakdang.cassdio.core.metadata.config.MetadataDbConfigProvider
import kr.hakdang.cassdio.core.metadata.cql.CqlExecutor
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID

@Repository
class InstallationStateRepository(
    private val configProvider: MetadataDbConfigProvider,
    private val cqlExecutor: CqlExecutor,
) {
    fun find(): InstallationState? {
        val keyspace = configProvider.getConfig().keyspace
        val row =
            cqlExecutor.queryOne(
                "SELECT installation_id, bootstrap_completed_at, schema_version, cassdio_version, initial_settings " +
                    "FROM $keyspace.installation_state WHERE id = 'default'",
            ) ?: return null

        return InstallationState(
            installationId = UUID.fromString(row.string("installation_id")),
            bootstrapCompletedAt = row.string("bootstrap_completed_at")?.let(Instant::parse),
            schemaVersion = row.string("schema_version"),
            cassdioVersion = row.string("cassdio_version") ?: "unknown",
            initialSettings = row.stringMap("initial_settings").orEmpty(),
        )
    }

    fun upsert(
        installationId: UUID,
        bootstrapCompletedAt: Instant?,
        schemaVersion: String?,
        cassdioVersion: String,
        initialSettings: Map<String, String>,
    ) {
        val keyspace = configProvider.getConfig().keyspace
        val completedAtLiteral = bootstrapCompletedAt?.timestampLiteral() ?: "null"
        val schemaVersionLiteral = schemaVersion?.cqlLiteral() ?: "null"

        cqlExecutor.execute(
            """
            INSERT INTO $keyspace.installation_state
            (id, installation_id, bootstrap_completed_at, schema_version, cassdio_version, initial_settings, updated_at)
            VALUES (
              'default',
              $installationId,
              $completedAtLiteral,
              $schemaVersionLiteral,
              ${cassdioVersion.cqlLiteral()},
              ${initialSettings.cqlMapLiteral()},
              ${Instant.now().timestampLiteral()}
            )
            """.trimIndent(),
        )
    }
}
