package kr.hakdang.cassdio.core.metadata.cluster

import kr.hakdang.cassdio.core.exception.ConflictException
import kr.hakdang.cassdio.core.exception.NotFoundException
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Instant
import java.util.UUID

@Service
class ManagedClusterService(
    private val repository: ManagedClusterRepository,
    private val connectionService: ClusterConnectionService,
    private val encryptionService: EncryptionService,
    private val healthChecker: ClusterHealthChecker,
    private val sessionManager: ManagedClusterSessionManager,
    private val clock: Clock = Clock.systemUTC(),
) {
    fun list(): List<ManagedClusterDetail> = repository.list().map(::detail)

    fun detail(clusterId: UUID): ManagedClusterDetail =
        detail(
            repository.findById(clusterId) ?: throw NotFoundException("Cluster not found."),
        )

    fun testConnection(request: ManagedClusterRegistrationRequest): ClusterConnectionTestResult =
        connectionService.testConnection(request).withCompatibilityCheck()

    fun create(request: ManagedClusterRegistrationRequest): ManagedClusterDetail {
        if (repository.existsByName(request.name)) {
            throw ConflictException("Cluster name already exists.")
        }

        val connectionResult = testConnection(request)
        if (!connectionResult.success) {
            throw ConflictException("Cluster connection failed: ${connectionResult.errorMessage ?: "unknown error"}")
        }

        val now = Instant.now(clock)
        val clusterId = UUID.randomUUID()
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
        val credential = request.toEncryptedCredential(clusterId, now)
        val healthSnapshot = healthChecker.createInitialSnapshot(clusterId, connectionResult)

        repository.save(cluster, credential, healthSnapshot)
        repository.recordAuditEvent(
            eventType = "CLUSTER_CREATED",
            targetType = "cluster",
            targetId = clusterId.toString(),
            details = mapOf("cluster_name" to request.name, "environment" to request.environment.name),
            createdAt = now,
        )

        return detail(cluster.copy())
    }

    fun update(
        clusterId: UUID,
        request: ManagedClusterUpdateRequest,
    ): ManagedClusterDetail {
        val current = repository.findById(clusterId) ?: throw NotFoundException("Cluster not found.")
        val credential = repository.findCredential(clusterId)
        val now = Instant.now(clock)
        if (request.name != null && request.name != current.name && repository.existsByName(request.name)) {
            throw ConflictException("Cluster name already exists.")
        }
        val nextProbe =
            ManagedClusterRegistrationRequest(
                name = request.name ?: current.name,
                environment = request.environment ?: current.environment,
                contactPoints = request.contactPoints ?: current.contactPoints,
                port = request.port ?: current.port,
                localDatacenter = request.localDatacenter ?: current.localDatacenter,
                username = request.username ?: encryptionService.decrypt(credential?.usernameCiphertext),
                password = request.password ?: encryptionService.decrypt(credential?.passwordCiphertext),
                secretReference = request.secretReference ?: encryptionService.decrypt(credential?.secretReferenceCiphertext),
                tlsEnabled = request.tlsEnabled ?: credential?.tlsEnabled ?: false,
                ownerMemberId = request.ownerMemberId ?: current.ownerMemberId,
                grantDbaToSuperAdmin = false,
            )
        val connectionResult = testConnection(nextProbe)
        if (!connectionResult.success) {
            repository.recordAuditEvent(
                eventType = "CLUSTER_UPDATE_CONNECTION_FAILED",
                targetType = "cluster",
                targetId = clusterId.toString(),
                details = mapOf("error" to (connectionResult.errorMessage ?: "unknown")),
                createdAt = now,
            )
            throw ConflictException("Cluster connection failed: ${connectionResult.errorMessage ?: "unknown error"}")
        }

        val updated =
            current.copy(
                name = nextProbe.name,
                environment = nextProbe.environment,
                contactPoints = nextProbe.contactPoints,
                port = nextProbe.port,
                localDatacenter = nextProbe.localDatacenter,
                cassandraVersion = connectionResult.cassandraVersion ?: current.cassandraVersion,
                keyspaceCount = connectionResult.keyspaceCount ?: current.keyspaceCount,
                tableCount = connectionResult.tableCount ?: current.tableCount,
                ownerMemberId = nextProbe.ownerMemberId,
                updatedAt = now,
            )

        repository.update(updated)
        repository.saveHealth(healthChecker.createInitialSnapshot(clusterId, connectionResult))

        if (request.rotateCredential || request.tlsEnabled != null) {
            repository.updateCredential(nextProbe.toEncryptedCredential(clusterId, now, credential?.createdAt ?: now))
        }

        sessionManager.clear(clusterId)
        repository.recordAuditEvent(
            eventType = "CLUSTER_UPDATED",
            targetType = "cluster",
            targetId = clusterId.toString(),
            details = mapOf("cluster_name" to updated.name, "credential_rotated" to request.rotateCredential.toString()),
            createdAt = now,
        )

        return detail(updated)
    }

    fun delete(clusterId: UUID): ManagedClusterDetail {
        val current = repository.findById(clusterId) ?: throw NotFoundException("Cluster not found.")
        val before = sessionManager.snapshot(clusterId)
        val now = Instant.now(clock)
        sessionManager.clear(clusterId)
        repository.markDeleted(current, now)
        repository.recordAuditEvent(
            eventType = "CLUSTER_DELETED",
            targetType = "cluster",
            targetId = clusterId.toString(),
            details = mapOf("cluster_name" to current.name, "previous_session_status" to before.status.name),
            createdAt = now,
        )
        return detail(current.copy(status = ManagedClusterStatus.DELETED, updatedAt = now))
    }

    fun clearSession(clusterId: UUID): ClusterSessionClearResult {
        repository.findById(clusterId) ?: throw NotFoundException("Cluster not found.")
        val before = sessionManager.snapshot(clusterId)
        sessionManager.clear(clusterId)
        val clearedAt = Instant.now(clock)
        val after = sessionManager.snapshot(clusterId)
        repository.recordAuditEvent(
            eventType = "CLUSTER_SESSION_CLEARED",
            targetType = "cluster",
            targetId = clusterId.toString(),
            details = mapOf("before" to before.status.name, "after" to after.status.name),
            createdAt = clearedAt,
        )
        return ClusterSessionClearResult(clearedCount = 1, clusterIds = listOf(clusterId), clearedAt = clearedAt)
    }

    fun clearAllSessions(): ClusterSessionClearResult {
        val clusters = repository.list()
        val ids = clusters.map { it.clusterId }
        val before = clusters.associate { it.clusterId.toString() to sessionManager.snapshot(it.clusterId).status.name }
        sessionManager.clearAll(ids)
        val clearedAt = Instant.now(clock)
        repository.recordAuditEvent(
            eventType = "ALL_CLUSTER_SESSIONS_CLEARED",
            targetType = "cluster_session",
            targetId = "all",
            details = before,
            createdAt = clearedAt,
        )
        return ClusterSessionClearResult(clearedCount = ids.size, clusterIds = ids, clearedAt = clearedAt)
    }

    private fun detail(cluster: ManagedCluster): ManagedClusterDetail =
        ManagedClusterDetail(
            cluster = cluster,
            credential = repository.findCredential(cluster.clusterId)?.summary(),
            health = repository.latestHealthSnapshot(cluster.clusterId),
            session = sessionManager.snapshot(cluster.clusterId),
            lastConnectionError = null,
        )

    private fun EncryptedClusterCredential.summary(): ClusterCredentialSummary =
        ClusterCredentialSummary(
            usernameConfigured = usernameCiphertext != null,
            passwordConfigured = passwordCiphertext != null,
            secretReferenceConfigured = secretReferenceCiphertext != null,
            tlsEnabled = tlsEnabled,
            updatedAt = updatedAt,
        )

    private fun ManagedClusterRegistrationRequest.toEncryptedCredential(
        clusterId: UUID,
        now: Instant,
        createdAt: Instant = now,
    ): EncryptedClusterCredential =
        EncryptedClusterCredential(
            clusterId = clusterId,
            usernameCiphertext = encryptionService.encrypt(username),
            passwordCiphertext = encryptionService.encrypt(password),
            secretReferenceCiphertext = encryptionService.encrypt(secretReference),
            tlsEnabled = tlsEnabled,
            sslSettingsCiphertext = encryptionService.encrypt("tlsEnabled=$tlsEnabled"),
            createdAt = createdAt,
            updatedAt = now,
        )

    private fun ClusterConnectionTestResult.withCompatibilityCheck(): ClusterConnectionTestResult {
        val version = cassandraVersion ?: return copy(failureCode = ClusterConnectionFailureCode.CONNECTION_FAILED)
        val major = version.substringBefore('.').toIntOrNull()
        if (major == null || major !in 3..5) {
            return copy(
                success = false,
                checks =
                    checks +
                        ClusterConnectionCheck(
                            name = "version_compatibility",
                            success = false,
                            message = "Cassandra $version is not supported. Supported versions are 3.x, 4.x, and 5.x.",
                        ),
                failureCode = ClusterConnectionFailureCode.VERSION_UNSUPPORTED,
            )
        }

        return copy(
            checks =
                checks +
                    ClusterConnectionCheck(
                        name = "version_compatibility",
                        success = true,
                        message = "Cassandra $version is supported.",
                    ),
            failureCode = null,
        )
    }
}
