package kr.hakdang.cassdio.core.metadata.bootstrap

import kr.hakdang.cassdio.core.metadata.config.MetadataBootstrapProperties
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class MetadataBootstrapRunner(
    private val properties: MetadataBootstrapProperties,
    private val bootstrapService: MetadataBootstrapService,
) : ApplicationRunner {
    override fun run(args: ApplicationArguments) {
        if (properties.enabled) {
            bootstrapService.bootstrap()
        }
    }
}
