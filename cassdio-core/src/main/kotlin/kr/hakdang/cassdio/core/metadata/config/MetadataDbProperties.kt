package kr.hakdang.cassdio.core.metadata.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "cassdio.metadata.cassandra")
data class MetadataDbProperties(
    var contactPoints: List<String> = listOf("127.0.0.1"),
    var port: Int = 9042,
    var localDatacenter: String = "datacenter1",
    var keyspace: String = "cassdio_meta",
    var username: String? = null,
    var password: String? = null,
    var tlsEnabled: Boolean = false,
)

@Configuration
class MetadataDbConfigConfiguration {
    @Bean
    fun metadataDbConfigProvider(properties: MetadataDbProperties): MetadataDbConfigProvider =
        MetadataDbConfigProvider {
            MetadataDbConfig(
                contactPoints = properties.contactPoints,
                port = properties.port,
                localDatacenter = properties.localDatacenter,
                keyspace = properties.keyspace,
                username = properties.username,
                password = properties.password,
                tlsEnabled = properties.tlsEnabled,
            ).also { it.validate() }
        }
}
