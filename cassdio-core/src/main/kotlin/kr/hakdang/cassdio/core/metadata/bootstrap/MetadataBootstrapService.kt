package kr.hakdang.cassdio.core.metadata.bootstrap

import kr.hakdang.cassdio.core.metadata.config.MetadataBootstrapProperties
import kr.hakdang.cassdio.core.metadata.config.MetadataDbConfigProvider
import kr.hakdang.cassdio.core.metadata.cluster.InitialManagedClusterRegistrationService
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.util.UUID

@Service
class MetadataBootstrapService(
    private val properties: MetadataBootstrapProperties,
    private val keyspaceCreationService: KeyspaceCreationService,
    private val schemaDefinitions: MetadataSchemaDefinitions,
    private val lockRepository: BootstrapLockRepository,
    private val migrationService: SchemaMigrationService,
    private val seedService: SeedService,
    private val installationStateRepository: InstallationStateRepository,
    private val migrationCatalog: MetadataMigrationCatalog,
    private val configProvider: MetadataDbConfigProvider,
    private val initialManagedClusterRegistrationService: InitialManagedClusterRegistrationService? = null,
    private val clock: Clock = Clock.systemUTC(),
) {
    fun bootstrap(): BootstrapResult {
        val startedAt = Instant.now(clock)
        keyspaceCreationService.ensureKeyspace()
        schemaDefinitions.ensureBootstrapTables()

        val lock =
            lockRepository.acquire(
                name = LOCK_NAME,
                owner = properties.owner,
                ttl = Duration.ofSeconds(properties.lockTtlSeconds),
            )

        return runCatching {
            val installationId = installationStateRepository.find()?.installationId ?: UUID.randomUUID()
            val executedMigrations = migrationService.migrate()
            val executedSeeds = seedService.seed()
            initialManagedClusterRegistrationService?.registerIfConfigured()
            val schemaVersion = migrationCatalog.migrations().maxOfOrNull { it.version }

            installationStateRepository.upsert(
                installationId = installationId,
                bootstrapCompletedAt = Instant.now(clock),
                schemaVersion = schemaVersion,
                cassdioVersion = properties.cassdioVersion,
                initialSettings = initialSettingsSnapshot(),
            )
            lockRepository.release(lock.name, lock.owner, BootstrapLockStatus.RELEASED)

            BootstrapResult(
                installationId = installationId,
                schemaVersion = schemaVersion,
                executedMigrations = executedMigrations,
                executedSeeds = executedSeeds,
                duration = Duration.between(startedAt, Instant.now(clock)),
            )
        }.getOrElse { error ->
            lockRepository.release(lock.name, lock.owner, BootstrapLockStatus.FAILED)
            throw MetadataBootstrapException("Metadata bootstrap failed.", error)
        }
    }

    companion object {
        const val LOCK_NAME = "metadata-bootstrap"
    }

    private fun initialSettingsSnapshot(): Map<String, String> {
        val config = configProvider.getConfig()

        return buildMap {
            put("metadata.source", config.source.name)
            put("metadata.contact_points", config.contactPoints.joinToString(","))
            put("metadata.port", config.port.toString())
            put("metadata.local_datacenter", config.localDatacenter)
            put("metadata.keyspace", config.keyspace)
            put("metadata.tls_enabled", config.tlsEnabled.toString())
            put("bootstrap.replication_strategy", properties.replication.strategy.name)
            put("bootstrap.replication_factor", properties.replication.factor.toString())
            put("bootstrap.durable_writes", properties.durableWrites.toString())
            if (properties.replication.datacenters.isNotEmpty()) {
                put(
                    "bootstrap.replication_datacenters",
                    properties.replication.datacenters.entries.joinToString(",") { (datacenter, factor) ->
                        "$datacenter=$factor"
                    },
                )
            }
        }
    }
}
