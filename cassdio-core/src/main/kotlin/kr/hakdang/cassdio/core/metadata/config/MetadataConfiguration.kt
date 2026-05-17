package kr.hakdang.cassdio.core.metadata.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

@Configuration
@EnableConfigurationProperties(MetadataDbProperties::class, MetadataBootstrapProperties::class)
class MetadataConfiguration {
    @Bean
    fun metadataClock(): Clock = Clock.systemUTC()
}
