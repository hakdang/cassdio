package kr.hakdang.cassdio.core.metadata.cluster

import kr.hakdang.cassdio.core.metadata.config.ManagedClusterEnvironment
import java.time.Instant
import java.util.UUID

data class ManagedClusterRegistrationRequest(
    val name: String,
    val environment: ManagedClusterEnvironment,
    val contactPoints: List<String>,
    val port: Int,
    val localDatacenter: String,
    val username: String?,
    val password: String?,
    val tlsEnabled: Boolean,
    val ownerMemberId: UUID,
    val grantDbaToSuperAdmin: Boolean,
) {
    init {
        require(name.isNotBlank()) { "Cluster name must not be blank." }
        require(contactPoints.isNotEmpty()) { "At least one contact point is required." }
        require(contactPoints.all { it.isNotBlank() }) { "Contact points must not be blank." }
        require(port in 1..65535) { "Cluster port must be between 1 and 65535." }
        require(localDatacenter.isNotBlank()) { "Local datacenter must not be blank." }
    }
}

data class ManagedCluster(
    val clusterId: UUID,
    val name: String,
    val environment: ManagedClusterEnvironment,
    val contactPoints: List<String>,
    val port: Int,
    val localDatacenter: String,
    val cassandraVersion: String,
    val keyspaceCount: Int,
    val tableCount: Int,
    val ownerMemberId: UUID,
    val createdAt: Instant,
    val updatedAt: Instant,
)

data class EncryptedClusterCredential(
    val clusterId: UUID,
    val usernameCiphertext: String?,
    val passwordCiphertext: String?,
    val tlsEnabled: Boolean,
    val sslSettingsCiphertext: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
)

data class ClusterConnectionCheck(
    val name: String,
    val success: Boolean,
    val message: String,
)

data class ClusterConnectionTestResult(
    val success: Boolean,
    val cassandraVersion: String?,
    val keyspaceCount: Int?,
    val tableCount: Int?,
    val checks: List<ClusterConnectionCheck>,
) {
    val errorMessage: String? =
        checks
            .firstOrNull { !it.success }
            ?.message
}

data class ClusterHealthSnapshot(
    val snapshotId: UUID,
    val clusterId: UUID,
    val status: ClusterHealthStatus,
    val nodeCount: Int,
    val upNodeCount: Int,
    val schemaAgreement: Boolean,
    val keyspaceCount: Int,
    val tableCount: Int,
    val pendingCompactions: Int?,
    val repairsStatus: String,
    val checkedAt: Instant,
)

enum class ClusterHealthStatus {
    HEALTHY,
    DEGRADED,
    UNHEALTHY,
}

data class InitialManagedClusterRegistrationResult(
    val registered: Boolean,
    val clusterId: UUID?,
    val message: String,
)
