package kr.hakdang.cassdio.core.metadata.bootstrap

import org.springframework.stereotype.Component

@Component
class MetadataSeedCatalog {
    fun seeds(): List<SeedDefinition> = emptyList()
}
