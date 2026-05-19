package kr.hakdang.cassdio.core.metadata.config

import kr.hakdang.cassdio.core.metadata.bootstrap.BCryptPasswordHashService
import kr.hakdang.cassdio.core.metadata.bootstrap.PasswordHashService
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

@Configuration
@EnableConfigurationProperties(MetadataDbProperties::class, MetadataBootstrapProperties::class)
class MetadataConfiguration {
    @Bean
    fun metadataClock(): Clock = Clock.systemUTC()

    @Bean
    fun metadataPasswordHashService(): PasswordHashService = BCryptPasswordHashService()
}
