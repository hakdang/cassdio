package kr.hakdang.cassdio.core.metadata.cluster

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.auth.ProgrammaticPlainTextAuthProvider
import com.datastax.oss.driver.api.core.cql.Row
import kr.hakdang.cassdio.core.metadata.cql.CqlRow
import org.springframework.stereotype.Service
import java.net.InetSocketAddress
import javax.net.ssl.SSLContext

interface ManagedClusterProbeClient : AutoCloseable {
    fun queryOne(statement: String): CqlRow?
}

interface ManagedClusterProbeClientFactory {
    fun create(request: ManagedClusterRegistrationRequest): ManagedClusterProbeClient
}

@Service
class CassandraManagedClusterProbeClientFactory : ManagedClusterProbeClientFactory {
    override fun create(request: ManagedClusterRegistrationRequest): ManagedClusterProbeClient {
        val builder =
            CqlSession
                .builder()
                .withLocalDatacenter(request.localDatacenter)

        request.contactPoints.forEach { contactPoint ->
            builder.addContactPoint(InetSocketAddress(contactPoint, request.port))
        }

        request.username?.takeIf { it.isNotBlank() }?.let { username ->
            builder.withAuthProvider(
                ProgrammaticPlainTextAuthProvider(
                    username,
                    request.password.orEmpty(),
                ),
            )
        }

        if (request.tlsEnabled) {
            builder.withSslContext(SSLContext.getDefault())
        }

        return CassandraManagedClusterProbeClient(builder.build())
    }
}

class CassandraManagedClusterProbeClient(
    private val session: CqlSession,
) : ManagedClusterProbeClient {
    override fun queryOne(statement: String): CqlRow? =
        session
            .execute(statement)
            .one()
            ?.toCqlRow()

    override fun close() {
        session.close()
    }

    private fun Row.toCqlRow(): CqlRow {
        val values =
            columnDefinitions.associate { definition ->
                val name = definition.name.asInternal()
                name to getObject(name)
            }

        return CqlRow(values)
    }
}

@Service
class ClusterConnectionService(
    private val clientFactory: ManagedClusterProbeClientFactory,
) {
    fun testConnection(request: ManagedClusterRegistrationRequest): ClusterConnectionTestResult {
        val checks = mutableListOf<ClusterConnectionCheck>()
        var cassandraVersion: String? = null
        var keyspaceCount: Int? = null
        var tableCount: Int? = null

        return runCatching {
            clientFactory.create(request).use { client ->
                cassandraVersion =
                    runCheck(checks, "cassandra_version", "Cassandra version was read.") {
                        client
                            .queryOne("SELECT release_version FROM system.local")
                            ?.string("release_version")
                            ?: error("system.local did not return release_version.")
                    }

                runCheck(checks, "system_keyspace_read", "System schema is readable.") {
                    client
                        .queryOne("SELECT keyspace_name FROM system_schema.keyspaces LIMIT 1")
                        ?.string("keyspace_name")
                        ?: error("system_schema.keyspaces did not return a row.")
                }

                keyspaceCount =
                    runCheck(checks, "keyspace_count", "Keyspace count was collected.") {
                        client
                            .queryOne("SELECT COUNT(*) AS keyspace_count FROM system_schema.keyspaces")
                            ?.number("keyspace_count")
                            ?: error("Keyspace count could not be collected.")
                    }

                tableCount =
                    runCheck(checks, "table_count", "Table count was collected.") {
                        client
                            .queryOne("SELECT COUNT(*) AS table_count FROM system_schema.tables")
                            ?.number("table_count")
                            ?: error("Table count could not be collected.")
                    }
            }

            ClusterConnectionTestResult(
                success = checks.all { it.success },
                cassandraVersion = cassandraVersion,
                keyspaceCount = keyspaceCount,
                tableCount = tableCount,
                checks = checks,
            )
        }.getOrElse { error ->
            checks +=
                ClusterConnectionCheck(
                    name = "connection",
                    success = false,
                    message = error.message ?: error::class.java.simpleName,
                )
            ClusterConnectionTestResult(
                success = false,
                cassandraVersion = cassandraVersion,
                keyspaceCount = keyspaceCount,
                tableCount = tableCount,
                checks = checks,
            )
        }
    }

    private fun <T> runCheck(
        checks: MutableList<ClusterConnectionCheck>,
        name: String,
        successMessage: String,
        block: () -> T,
    ): T =
        runCatching(block)
            .onSuccess {
                checks += ClusterConnectionCheck(name = name, success = true, message = successMessage)
            }.getOrElse { error ->
                checks +=
                    ClusterConnectionCheck(
                        name = name,
                        success = false,
                        message = error.message ?: error::class.java.simpleName,
                    )
                throw error
            }

    private fun CqlRow.number(name: String): Int? = string(name)?.toLongOrNull()?.toInt()
}
