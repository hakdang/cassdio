package kr.hakdang.cassdio.core.metadata.cql

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

class MetadataCqlUnavailableException(
    message: String = "Metadata Cassandra CQL executor is not configured.",
) : RuntimeException(message)

@Configuration
class CqlExecutorConfiguration {
    @Bean
    @ConditionalOnMissingBean(CqlExecutor::class)
    fun unavailableCqlExecutor(): CqlExecutor =
        object : CqlExecutor {
            override fun execute(statement: String): Unit = throw MetadataCqlUnavailableException()

            override fun queryOne(statement: String): CqlRow? = throw MetadataCqlUnavailableException()
        }
}
