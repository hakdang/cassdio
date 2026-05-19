package kr.hakdang.cassdio.core.metadata.cluster

import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Instant
import java.util.UUID

@Service
class ClusterHealthChecker(
    private val clock: Clock = Clock.systemUTC(),
) {
    fun createInitialSnapshot(
        clusterId: UUID,
        connectionResult: ClusterConnectionTestResult,
    ): ClusterHealthSnapshot {
        val status =
            when {
                !connectionResult.success -> ClusterHealthStatus.UNHEALTHY
                connectionResult.keyspaceCount == null || connectionResult.tableCount == null -> ClusterHealthStatus.DEGRADED
                else -> ClusterHealthStatus.HEALTHY
            }

        return ClusterHealthSnapshot(
            snapshotId = UUID.nameUUIDFromBytes("phase-2-m4-health-$clusterId".toByteArray(Charsets.UTF_8)),
            clusterId = clusterId,
            status = status,
            nodeCount = 1,
            upNodeCount = if (connectionResult.success) 1 else 0,
            schemaAgreement = connectionResult.success,
            keyspaceCount = connectionResult.keyspaceCount ?: 0,
            tableCount = connectionResult.tableCount ?: 0,
            pendingCompactions = null,
            repairsStatus = "UNKNOWN",
            checkedAt = Instant.now(clock),
        )
    }
}
