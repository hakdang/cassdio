package kr.hakdang.cassdio.core.metadata.cql

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.CqlSessionBuilder
import com.datastax.oss.driver.api.core.auth.ProgrammaticPlainTextAuthProvider
import com.datastax.oss.driver.api.core.cql.Row
import kr.hakdang.cassdio.core.metadata.config.MetadataDbConfig
import kr.hakdang.cassdio.core.metadata.config.MetadataDbConfigProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.InetSocketAddress
import javax.net.ssl.SSLContext

class CassandraCqlExecutor(
    private val configProvider: MetadataDbConfigProvider,
    private val sessionFactory: (MetadataDbConfig) -> CqlSession = ::buildSession,
) : CqlExecutor,
    AutoCloseable {
    @Volatile
    private var session: CqlSession? = null

    @Volatile
    private var activeConfig: MetadataDbConfig? = null

    override fun execute(statement: String) {
        session().execute(statement)
    }

    override fun queryOne(statement: String): CqlRow? =
        session()
            .execute(statement)
            .one()
            ?.toCqlRow()

    override fun query(statement: String): List<CqlRow> =
        session()
            .execute(statement)
            .map { it.toCqlRow() }
            .toList()

    override fun close() {
        session?.close()
        session = null
        activeConfig = null
    }

    private fun session(): CqlSession {
        val currentConfig = configProvider.getConfig()
        val currentSession = session

        if (currentSession != null && activeConfig == currentConfig) {
            return currentSession
        }

        return synchronized(this) {
            if (session != null && activeConfig == currentConfig) {
                session as CqlSession
            } else {
                session?.close()
                sessionFactory(currentConfig).also {
                    session = it
                    activeConfig = currentConfig
                }
            }
        }
    }

    private fun Row.toCqlRow(): CqlRow {
        val values =
            columnDefinitions.associate { definition ->
                val name = definition.name.asInternal()
                name to getObject(name)
            }

        return CqlRow(values)
    }

    companion object {
        private fun buildSession(config: MetadataDbConfig): CqlSession {
            val builder =
                CqlSession
                    .builder()
                    .withLocalDatacenter(config.localDatacenter)

            config.contactPoints.forEach { contactPoint ->
                builder.addContactPoint(InetSocketAddress(contactPoint, config.port))
            }

            config.username?.takeIf { it.isNotBlank() }?.let { username ->
                builder.withAuthProvider(
                    ProgrammaticPlainTextAuthProvider(
                        username,
                        config.password.orEmpty(),
                    ),
                )
            }

            if (config.tlsEnabled) {
                builder.withSslContext(SSLContext.getDefault())
            }

            return builder.build()
        }
    }
}

@Configuration
@ConditionalOnClass(CqlSessionBuilder::class)
class CassandraCqlExecutorConfiguration {
    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean(CqlExecutor::class)
    fun cassandraCqlExecutor(configProvider: MetadataDbConfigProvider): CqlExecutor = CassandraCqlExecutor(configProvider)
}
