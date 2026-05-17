package kr.hakdang.cassdio.core.metadata.bootstrap

import kr.hakdang.cassdio.core.metadata.config.MetadataBootstrapProperties
import kr.hakdang.cassdio.core.metadata.config.MetadataDbConfigProvider
import kr.hakdang.cassdio.core.metadata.config.ReplicationStrategy
import kr.hakdang.cassdio.core.metadata.cql.CqlExecutor
import org.springframework.stereotype.Service

@Service
class KeyspaceCreationService(
    private val configProvider: MetadataDbConfigProvider,
    private val bootstrapProperties: MetadataBootstrapProperties,
    private val cqlExecutor: CqlExecutor,
) {
    fun ensureKeyspace(): Boolean {
        val config = configProvider.getConfig()
        val existing =
            cqlExecutor.queryOne(
                "SELECT keyspace_name FROM system_schema.keyspaces " +
                    "WHERE keyspace_name = ${config.keyspace.cqlLiteral()}",
            )

        if (existing != null) {
            return false
        }

        cqlExecutor.execute(createKeyspaceStatement(config.keyspace))
        return true
    }

    fun createKeyspaceStatement(keyspace: String): String {
        val replication =
            when (bootstrapProperties.replication.strategy) {
                ReplicationStrategy.SIMPLE ->
                    "{'class': 'SimpleStrategy', 'replication_factor': " +
                        bootstrapProperties.replication.factor +
                        "}"

                ReplicationStrategy.NETWORK_TOPOLOGY ->
                    networkTopologyReplication()
            }

        return "CREATE KEYSPACE IF NOT EXISTS $keyspace " +
            "WITH replication = $replication " +
            "AND durable_writes = ${bootstrapProperties.durableWrites.cqlLiteral()}"
    }

    private fun networkTopologyReplication(): String {
        val datacenters = bootstrapProperties.replication.datacenters
        require(datacenters.isNotEmpty()) {
            "NetworkTopologyStrategy requires at least one datacenter replication factor."
        }

        return datacenters.entries.joinToString(
            separator = ", ",
            prefix = "{'class': 'NetworkTopologyStrategy', ",
            postfix = "}",
        ) { (datacenter, factor) -> "${datacenter.cqlLiteral()}: $factor" }
    }
}
