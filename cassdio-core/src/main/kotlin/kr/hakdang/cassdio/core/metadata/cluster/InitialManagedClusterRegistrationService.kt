package kr.hakdang.cassdio.core.metadata.cluster

import kr.hakdang.cassdio.core.metadata.bootstrap.MetadataBootstrapException
import kr.hakdang.cassdio.core.metadata.bootstrap.MetadataSeedCatalog
import kr.hakdang.cassdio.core.metadata.config.InitialManagedClusterProperties
import kr.hakdang.cassdio.core.metadata.config.MetadataBootstrapProperties
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Instant
import java.util.UUID

@Service
class InitialManagedClusterRegistrationService(
    private val properties: MetadataBootstrapProperties,
    private val connectionService: ClusterConnectionService,
    private val encryptionService: EncryptionService,
    private val healthChecker: ClusterHealthChecker,
    private val repository: ManagedClusterRepository,
    private val clock: Clock = Clock.systemUTC(),
) {
    fun registerIfConfigured(): InitialManagedClusterRegistrationResult {
        val initialCluster = properties.initialCluster
        if (!initialCluster.enabled) {
            return InitialManagedClusterRegistrationResult(
                registered = false,
                clusterId = null,
                message = "Initial managed cluster registration is disabled.",
            )
        }

        if (repository.existsByName(initialCluster.name)) {
            return InitialManagedClusterRegistrationResult(
                registered = false,
                clusterId = null,
                message = "Managed cluster '${initialCluster.name}' already exists.",
            )
        }

        val request = initialCluster.toRegistrationRequest()
        val connectionResult = connectionService.testConnection(request)
        if (!connectionResult.success) {
            throw MetadataBootstrapException(
                "Initial managed cluster connection failed: ${connectionResult.errorMessage ?: "unknown error"}",
            )
        }

        val now = Instant.now(clock)
        val clusterId = UUID.nameUUIDFromBytes("phase-2-m4-cluster-${request.name}".toByteArray(Charsets.UTF_8))
        val cluster =
            ManagedCluster(
                clusterId = clusterId,
                name = request.name,
                environment = request.environment,
                contactPoints = request.contactPoints,
                port = request.port,
                localDatacenter = request.localDatacenter,
                cassandraVersion = connectionResult.cassandraVersion ?: "unknown",
                keyspaceCount = connectionResult.keyspaceCount ?: 0,
                tableCount = connectionResult.tableCount ?: 0,
                ownerMemberId = request.ownerMemberId,
                createdAt = now,
                updatedAt = now,
            )
        val credential =
            EncryptedClusterCredential(
                clusterId = clusterId,
                usernameCiphertext = encryptionService.encrypt(request.username),
                passwordCiphertext = encryptionService.encrypt(request.password),
                secretReferenceCiphertext = encryptionService.encrypt(request.secretReference),
                tlsEnabled = request.tlsEnabled,
                sslSettingsCiphertext = encryptionService.encrypt("tlsEnabled=${request.tlsEnabled}"),
                createdAt = now,
                updatedAt = now,
            )
        val healthSnapshot = healthChecker.createInitialSnapshot(clusterId, connectionResult)

        repository.save(cluster, credential, healthSnapshot)

        if (request.grantDbaToSuperAdmin) {
            repository.grantDbaToSuperAdmin(clusterId, now)
        }

        repository.recordAuditEvent(
            eventType = "INITIAL_CLUSTER_REGISTERED",
            targetType = "cluster",
            targetId = clusterId.toString(),
            details =
                mapOf(
                    "phase" to "2",
                    "milestone" to "4",
                    "cluster_name" to request.name,
                    "environment" to request.environment.name,
                    "dba_granted" to request.grantDbaToSuperAdmin.toString(),
                ),
            createdAt = now,
        )

        return InitialManagedClusterRegistrationResult(
            registered = true,
            clusterId = clusterId,
            message = "Initial managed cluster '${request.name}' registered.",
        )
    }

    private fun InitialManagedClusterProperties.toRegistrationRequest(): ManagedClusterRegistrationRequest =
        ManagedClusterRegistrationRequest(
            name = name,
            environment = environment,
            contactPoints = contactPoints,
            port = port,
            localDatacenter = localDatacenter,
            username = username,
            password = password,
            tlsEnabled = tlsEnabled,
            ownerMemberId = ownerMemberId?.let(UUID::fromString) ?: MetadataSeedCatalog.SUPER_ADMIN_MEMBER_ID,
            grantDbaToSuperAdmin = grantDbaToSuperAdmin,
        )
}
