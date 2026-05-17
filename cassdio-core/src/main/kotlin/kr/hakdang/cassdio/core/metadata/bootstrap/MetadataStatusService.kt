package kr.hakdang.cassdio.core.metadata.bootstrap

import kr.hakdang.cassdio.core.metadata.config.MetadataDbConfigProvider
import kr.hakdang.cassdio.core.metadata.cql.CqlExecutor
import kr.hakdang.cassdio.core.metadata.cql.MetadataCqlUnavailableException
import org.springframework.stereotype.Service

@Service
class MetadataStatusService(
    private val configProvider: MetadataDbConfigProvider,
    private val cqlExecutor: CqlExecutor,
    private val installationStateRepository: InstallationStateRepository,
) {
    fun status(): MetadataStatus {
        val config = configProvider.getConfig()
        val connected = canConnect()
        val state =
            if (connected) {
                runCatching { installationStateRepository.find() }.getOrNull()
            } else {
                null
            }

        return MetadataStatus(
            configured = true,
            source = config.source.name,
            keyspace = config.keyspace,
            connected = connected,
            schemaVersion = state?.schemaVersion,
            bootstrapCompleted = state?.bootstrapCompletedAt != null,
            installationId = state?.installationId,
        )
    }

    private fun canConnect(): Boolean =
        runCatching {
            cqlExecutor.queryOne("SELECT release_version FROM system.local")
            true
        }.getOrElse { error ->
            if (error is MetadataCqlUnavailableException) {
                false
            } else {
                false
            }
        }
}
