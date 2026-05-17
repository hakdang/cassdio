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
                seed.statements.forEach(cqlExecutor::execute)
                seedHistoryRepository.record(seed)
                seed.idempotencyKey
            }
}
