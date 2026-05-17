package kr.hakdang.cassdio.core.metadata.bootstrap

import kr.hakdang.cassdio.core.metadata.cql.CqlExecutor
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Duration
import java.time.Instant

@Service
class SchemaMigrationService(
    private val migrationCatalog: MetadataMigrationCatalog,
    private val migrationRepository: SchemaMigrationRepository,
    private val cqlExecutor: CqlExecutor,
    private val clock: Clock = Clock.systemUTC(),
) {
    fun migrate(): List<String> =
        migrationCatalog
            .migrations()
            .sortedBy { it.version }
            .filterNot { migrationRepository.isApplied(it.version) }
            .map { migration ->
                execute(migration)
                migration.version
            }

    private fun execute(migration: MetadataMigration) {
        val startedAt = Instant.now(clock)

        runCatching {
            migration.statements.forEach(cqlExecutor::execute)
        }.onSuccess {
            migrationRepository.record(
                migration = migration,
                executedAt = startedAt,
                executionTimeMillis = elapsedMillis(startedAt),
                success = true,
            )
        }.onFailure { error ->
            migrationRepository.record(
                migration = migration,
                executedAt = startedAt,
                executionTimeMillis = elapsedMillis(startedAt),
                success = false,
            )
            throw MetadataBootstrapException("Failed to execute metadata migration ${migration.version}.", error)
        }
    }

    private fun elapsedMillis(startedAt: Instant): Int =
        Duration
            .between(startedAt, Instant.now(clock))
            .toMillis()
            .coerceAtMost(Int.MAX_VALUE.toLong())
            .toInt()
}
