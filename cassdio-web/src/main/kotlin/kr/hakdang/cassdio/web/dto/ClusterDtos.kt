package kr.hakdang.cassdio.web.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Positive
import kr.hakdang.cassdio.core.metadata.bootstrap.MetadataSeedCatalog
import kr.hakdang.cassdio.core.metadata.cluster.ClusterConnectionCheck
import kr.hakdang.cassdio.core.metadata.cluster.ClusterConnectionTestResult
import kr.hakdang.cassdio.core.metadata.cluster.ClusterCredentialSummary
import kr.hakdang.cassdio.core.metadata.cluster.ClusterHealthSnapshot
import kr.hakdang.cassdio.core.metadata.cluster.ClusterSessionClearResult
import kr.hakdang.cassdio.core.metadata.cluster.ClusterSessionSnapshot
import kr.hakdang.cassdio.core.metadata.cluster.ManagedCluster
import kr.hakdang.cassdio.core.metadata.cluster.ManagedClusterDetail
import kr.hakdang.cassdio.core.metadata.cluster.ManagedClusterRegistrationRequest
import kr.hakdang.cassdio.core.metadata.cluster.ManagedClusterUpdateRequest
import kr.hakdang.cassdio.core.metadata.config.ManagedClusterEnvironment
import java.time.Instant
import java.util.UUID

data class ClusterCreateRequest(
    @field:NotBlank val name: String,
    val environment: String = ManagedClusterEnvironment.DEV.name,
    @field:NotEmpty val contactPoints: List<String>,
    @field:Positive val port: Int = 9042,
    @field:NotBlank val localDatacenter: String,
    val username: String? = null,
    val password: String? = null,
    val secretReference: String? = null,
    val tlsEnabled: Boolean = false,
    val ownerMemberId: UUID? = null,
    val grantDbaToSuperAdmin: Boolean = false,
)

data class ClusterUpdateRequest(
    val name: String? = null,
    val environment: String? = null,
    val contactPoints: List<String>? = null,
    val port: Int? = null,
    val localDatacenter: String? = null,
    val username: String? = null,
    val password: String? = null,
    val secretReference: String? = null,
    val rotateCredential: Boolean = false,
    val tlsEnabled: Boolean? = null,
    val ownerMemberId: UUID? = null,
)

data class ClusterResponse(
    val clusterId: UUID,
    val name: String,
    val environment: String,
    val contactPoints: List<String>,
    val port: Int,
    val localDatacenter: String,
    val cassandraVersion: String,
    val keyspaceCount: Int,
    val tableCount: Int,
    val ownerMemberId: UUID,
    val status: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val credential: ClusterCredentialResponse?,
    val health: ClusterHealthResponse?,
    val session: ClusterSessionResponse,
    val lastConnectionError: String?,
)

data class ClusterCredentialResponse(
    val usernameConfigured: Boolean,
    val passwordConfigured: Boolean,
    val secretReferenceConfigured: Boolean,
    val tlsEnabled: Boolean,
    val updatedAt: Instant,
)

data class ClusterHealthResponse(
    val status: String,
    val nodeCount: Int,
    val upNodeCount: Int,
    val schemaAgreement: Boolean,
    val keyspaceCount: Int,
    val tableCount: Int,
    val pendingCompactions: Int?,
    val repairsStatus: String,
    val checkedAt: Instant,
)

data class ClusterSessionResponse(
    val status: String,
    val openedAt: Instant?,
    val lastUsedAt: Instant?,
    val lastClearedAt: Instant?,
    val lastError: String?,
)

data class ClusterConnectionTestResponse(
    val success: Boolean,
    val cassandraVersion: String?,
    val keyspaceCount: Int?,
    val tableCount: Int?,
    val failureCode: String?,
    val checks: List<ClusterConnectionCheckResponse>,
)

data class ClusterConnectionCheckResponse(
    val name: String,
    val success: Boolean,
    val message: String,
)

data class ClusterSessionClearResponse(
    val clearedCount: Int,
    val clusterIds: List<UUID>,
    val clearedAt: Instant,
)

fun ClusterCreateRequest.toCoreRequest(): ManagedClusterRegistrationRequest =
    ManagedClusterRegistrationRequest(
        name = name,
        environment = ManagedClusterEnvironment.valueOf(environment),
        contactPoints = contactPoints,
        port = port,
        localDatacenter = localDatacenter,
        username = username,
        password = password,
        secretReference = secretReference,
        tlsEnabled = tlsEnabled,
        ownerMemberId = ownerMemberId ?: MetadataSeedCatalog.SUPER_ADMIN_MEMBER_ID,
        grantDbaToSuperAdmin = grantDbaToSuperAdmin,
    )

fun ClusterUpdateRequest.toCoreRequest(): ManagedClusterUpdateRequest =
    ManagedClusterUpdateRequest(
        name = name,
        environment = environment?.let(ManagedClusterEnvironment::valueOf),
        contactPoints = contactPoints,
        port = port,
        localDatacenter = localDatacenter,
        username = username,
        password = password,
        secretReference = secretReference,
        rotateCredential = rotateCredential,
        tlsEnabled = tlsEnabled,
        ownerMemberId = ownerMemberId,
    )

fun ManagedClusterDetail.toResponse(): ClusterResponse =
    cluster.toResponse(credential = credential, health = health, session = session, lastConnectionError = lastConnectionError)

private fun ManagedCluster.toResponse(
    credential: ClusterCredentialSummary?,
    health: ClusterHealthSnapshot?,
    session: ClusterSessionSnapshot,
    lastConnectionError: String?,
): ClusterResponse =
    ClusterResponse(
        clusterId = clusterId,
        name = name,
        environment = environment.name,
        contactPoints = contactPoints,
        port = port,
        localDatacenter = localDatacenter,
        cassandraVersion = cassandraVersion,
        keyspaceCount = keyspaceCount,
        tableCount = tableCount,
        ownerMemberId = ownerMemberId,
        status = status.name,
        createdAt = createdAt,
        updatedAt = updatedAt,
        credential = credential?.toResponse(),
        health = health?.toResponse(),
        session = session.toResponse(),
        lastConnectionError = lastConnectionError,
    )

private fun ClusterCredentialSummary.toResponse(): ClusterCredentialResponse =
    ClusterCredentialResponse(usernameConfigured, passwordConfigured, secretReferenceConfigured, tlsEnabled, updatedAt)

private fun ClusterHealthSnapshot.toResponse(): ClusterHealthResponse =
    ClusterHealthResponse(
        status = status.name,
        nodeCount = nodeCount,
        upNodeCount = upNodeCount,
        schemaAgreement = schemaAgreement,
        keyspaceCount = keyspaceCount,
        tableCount = tableCount,
        pendingCompactions = pendingCompactions,
        repairsStatus = repairsStatus,
        checkedAt = checkedAt,
    )

private fun ClusterSessionSnapshot.toResponse(): ClusterSessionResponse =
    ClusterSessionResponse(status.name, openedAt, lastUsedAt, lastClearedAt, lastError)

fun ClusterConnectionTestResult.toResponse(): ClusterConnectionTestResponse =
    ClusterConnectionTestResponse(
        success = success,
        cassandraVersion = cassandraVersion,
        keyspaceCount = keyspaceCount,
        tableCount = tableCount,
        failureCode = failureCode?.name,
        checks = checks.map { it.toResponse() },
    )

private fun ClusterConnectionCheck.toResponse(): ClusterConnectionCheckResponse = ClusterConnectionCheckResponse(name, success, message)

fun ClusterSessionClearResult.toResponse(): ClusterSessionClearResponse = ClusterSessionClearResponse(clearedCount, clusterIds, clearedAt)
