package kr.hakdang.cassdio.core.metadata.bootstrap

import kr.hakdang.cassdio.core.metadata.cql.CqlExecutor
import org.springframework.stereotype.Service

@Service
class SeedService(
    private val seedCatalog: MetadataSeedCatalog,
    private val seedHistoryRepository: SeedHistoryRepository,
    private val cqlExecutor: CqlExecutor,
) {
    fun seed(): List<String> =
        seedCatalog
            .seeds()
            .filterNot { seedHistoryRepository.isApplied(it.idempotencyKey) }
            .map { seed ->
                runCatching {
                    seed.statements.forEach(cqlExecutor::execute)
                }.onSuccess {
                    seedHistoryRepository.record(seed, success = true)
                }.onFailure { error ->
                    seedHistoryRepository.record(seed, success = false)
                    throw MetadataBootstrapException("Failed to execute metadata seed ${seed.idempotencyKey}.", error)
                }
                seed.idempotencyKey
            }
}
