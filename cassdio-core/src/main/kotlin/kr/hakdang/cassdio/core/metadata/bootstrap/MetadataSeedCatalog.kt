package kr.hakdang.cassdio.core.metadata.bootstrap

import org.springframework.stereotype.Component

@Component
open class MetadataSeedCatalog {
    open fun seeds(): List<SeedDefinition> = emptyList()
}
