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
    val secretReference: String? = null,
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
    val status: ManagedClusterStatus = ManagedClusterStatus.ACTIVE,
)

data class EncryptedClusterCredential(
    val clusterId: UUID,
    val usernameCiphertext: String?,
    val passwordCiphertext: String?,
    val secretReferenceCiphertext: String?,
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
    val failureCode: ClusterConnectionFailureCode? = null,
) {
    val errorMessage: String? =
        checks
            .firstOrNull { !it.success }
            ?.message
}

data class ManagedClusterUpdateRequest(
    val name: String?,
    val environment: ManagedClusterEnvironment?,
    val contactPoints: List<String>?,
    val port: Int?,
    val localDatacenter: String?,
    val username: String?,
    val password: String?,
    val secretReference: String?,
    val rotateCredential: Boolean,
    val tlsEnabled: Boolean?,
    val ownerMemberId: UUID?,
) {
    init {
        require(name == null || name.isNotBlank()) { "Cluster name must not be blank." }
        require(contactPoints == null || contactPoints.isNotEmpty()) { "At least one contact point is required." }
        require(contactPoints == null || contactPoints.all { it.isNotBlank() }) { "Contact points must not be blank." }
        require(port == null || port in 1..65535) { "Cluster port must be between 1 and 65535." }
        require(localDatacenter == null || localDatacenter.isNotBlank()) { "Local datacenter must not be blank." }
    }
}

data class ManagedClusterDetail(
    val cluster: ManagedCluster,
    val credential: ClusterCredentialSummary?,
    val health: ClusterHealthSnapshot?,
    val session: ClusterSessionSnapshot,
    val lastConnectionError: String?,
)

data class ClusterCredentialSummary(
    val usernameConfigured: Boolean,
    val passwordConfigured: Boolean,
    val secretReferenceConfigured: Boolean,
    val tlsEnabled: Boolean,
    val updatedAt: Instant,
)

data class ClusterSessionSnapshot(
    val status: ClusterSessionStatus,
    val openedAt: Instant?,
    val lastUsedAt: Instant?,
    val lastClearedAt: Instant?,
    val lastError: String?,
)

data class ClusterSessionClearResult(
    val clearedCount: Int,
    val clusterIds: List<UUID>,
    val clearedAt: Instant,
)

enum class ManagedClusterStatus {
    ACTIVE,
    DELETED,
}

enum class ClusterSessionStatus {
    DISCONNECTED,
    CONNECTED,
    ERROR,
}

enum class ClusterConnectionFailureCode {
    CONNECTION_FAILED,
    VERSION_UNSUPPORTED,
    SYSTEM_SCHEMA_UNREADABLE,
    METADATA_UNAVAILABLE,
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
