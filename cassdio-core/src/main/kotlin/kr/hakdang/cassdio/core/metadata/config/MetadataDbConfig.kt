package kr.hakdang.cassdio.core.metadata.config

data class MetadataDbConfig(
    val contactPoints: List<String>,
    val port: Int,
    val localDatacenter: String,
    val keyspace: String,
    val username: String? = null,
    val password: String? = null,
    val tlsEnabled: Boolean = false,
    val source: MetadataDbConfigSource = MetadataDbConfigSource.APPLICATION_CONFIG,
) {
    fun masked(): MetadataDbConfig =
        copy(
            password = password?.let { MASKED_SECRET },
        )

    fun validate() {
        require(contactPoints.isNotEmpty()) { "Metadata Cassandra contact points must not be empty." }
        require(contactPoints.none { it.isBlank() }) { "Metadata Cassandra contact points must not contain blank values." }
        require(port in 1..65535) { "Metadata Cassandra port must be between 1 and 65535." }
        require(localDatacenter.isNotBlank()) { "Metadata Cassandra local datacenter must not be blank." }
        require(keyspace.matches(KEYSPACE_NAME_PATTERN)) {
            "Metadata Cassandra keyspace must match ${KEYSPACE_NAME_PATTERN.pattern}."
        }
    }

    companion object {
        private const val MASKED_SECRET = "******"
        private val KEYSPACE_NAME_PATTERN = Regex("[a-zA-Z][a-zA-Z0-9_]{0,47}")
    }
}

enum class MetadataDbConfigSource {
    APPLICATION_CONFIG,
    TEST_FIXTURE,
}
