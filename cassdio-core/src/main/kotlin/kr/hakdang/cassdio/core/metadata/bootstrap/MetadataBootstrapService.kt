package kr.hakdang.cassdio.core.metadata.bootstrap

import kr.hakdang.cassdio.core.metadata.config.MetadataBootstrapProperties
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
    private val clock: Clock = Clock.systemUTC(),
) {
    fun bootstrap(): BootstrapResult {
        val startedAt = Instant.now(clock)
        val lock =
            lockRepository.acquire(
                name = LOCK_NAME,
                owner = properties.owner,
                ttl = Duration.ofSeconds(properties.lockTtlSeconds),
            )

        return runCatching {
            keyspaceCreationService.ensureKeyspace()
            schemaDefinitions.ensureBootstrapTables()

            val installationId = installationStateRepository.find()?.installationId ?: UUID.randomUUID()
            val executedMigrations = migrationService.migrate()
            val executedSeeds = seedService.seed()
            val schemaVersion = migrationCatalog.migrations().maxOfOrNull { it.version }

            installationStateRepository.upsert(
                installationId = installationId,
                bootstrapCompletedAt = Instant.now(clock),
                schemaVersion = schemaVersion,
                cassdioVersion = properties.cassdioVersion,
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
}
