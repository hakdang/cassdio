package kr.hakdang.cassdio.core.metadata.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "cassdio.metadata.bootstrap")
data class MetadataBootstrapProperties(
    var enabled: Boolean = false,
    var owner: String = "cassdio-web",
    var cassdioVersion: String = "0.1.0",
    var replication: ReplicationProperties = ReplicationProperties(),
    var durableWrites: Boolean = true,
    var lockTtlSeconds: Long = 300,
    var seed: MetadataSeedProperties = MetadataSeedProperties(),
    var encryption: MetadataEncryptionProperties = MetadataEncryptionProperties(),
    var initialCluster: InitialManagedClusterProperties = InitialManagedClusterProperties(),
)

data class ReplicationProperties(
    var strategy: ReplicationStrategy = ReplicationStrategy.SIMPLE,
    var factor: Int = 1,
    var datacenters: Map<String, Int> = emptyMap(),
)

enum class ReplicationStrategy {
    SIMPLE,
    NETWORK_TOPOLOGY,
}

data class MetadataSeedProperties(
    var workspace: DefaultWorkspaceProperties = DefaultWorkspaceProperties(),
    var superAdmin: SuperAdminProperties = SuperAdminProperties(),
)

data class DefaultWorkspaceProperties(
    var name: String = "Default",
    var description: String = "Default Cassdio workspace",
    var locale: String = "ko-KR",
    var timezone: String = "Asia/Seoul",
    var signupMode: String = "MANUAL_APPROVAL",
)

data class SuperAdminProperties(
    var displayName: String = "Super Admin",
    var email: String = "admin@cassdio.local",
    var initialPassword: String = "ChangeMe!2026",
)

data class MetadataEncryptionProperties(
    var masterKey: String? = null,
)

data class InitialManagedClusterProperties(
    var enabled: Boolean = false,
    var name: String = "Initial Cluster",
    var environment: ManagedClusterEnvironment = ManagedClusterEnvironment.DEV,
    var contactPoints: List<String> = listOf("127.0.0.1"),
    var port: Int = 9042,
    var localDatacenter: String = "datacenter1",
    var username: String? = null,
    var password: String? = null,
    var tlsEnabled: Boolean = false,
    var ownerMemberId: String? = null,
    var grantDbaToSuperAdmin: Boolean = true,
)

enum class ManagedClusterEnvironment {
    DEV,
    STAGING,
    PROD,
}
