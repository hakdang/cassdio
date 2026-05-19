package kr.hakdang.cassdio.core.metadata.cluster

import kr.hakdang.cassdio.core.metadata.bootstrap.MetadataSeedCatalog
import kr.hakdang.cassdio.core.metadata.config.MetadataDbConfigProvider
import kr.hakdang.cassdio.core.metadata.cql.CqlExecutor
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID

@Repository
class ManagedClusterRepository(
    private val configProvider: MetadataDbConfigProvider,
    private val cqlExecutor: CqlExecutor,
) {
    fun existsByName(name: String): Boolean {
        val keyspace = configProvider.getConfig().keyspace

        return cqlExecutor.queryOne(
            "SELECT cluster_id FROM $keyspace.managed_clusters_by_name WHERE name = ${name.cqlLiteral()}",
        ) != null
    }

    fun save(
        cluster: ManagedCluster,
        credential: EncryptedClusterCredential,
        healthSnapshot: ClusterHealthSnapshot,
    ) {
        val keyspace = configProvider.getConfig().keyspace

        cqlExecutor.execute(
            """
            INSERT INTO $keyspace.managed_clusters
            (cluster_id, name, environment, contact_points, port, local_datacenter, cassandra_version, keyspace_count, table_count, owner_member_id, created_at, updated_at, status)
            VALUES (
              ${cluster.clusterId},
              ${cluster.name.cqlLiteral()},
              ${cluster.environment.name.cqlLiteral()},
              ${cluster.contactPoints.cqlListLiteral()},
              ${cluster.port},
              ${cluster.localDatacenter.cqlLiteral()},
              ${cluster.cassandraVersion.cqlLiteral()},
              ${cluster.keyspaceCount},
              ${cluster.tableCount},
              ${cluster.ownerMemberId},
              ${cluster.createdAt.timestampLiteral()},
              ${cluster.updatedAt.timestampLiteral()},
              'ACTIVE'
            )
            """.trimIndent(),
        )
        cqlExecutor.execute(
            """
            INSERT INTO $keyspace.managed_clusters_by_name
            (name, cluster_id, environment, status, updated_at)
            VALUES (
              ${cluster.name.cqlLiteral()},
              ${cluster.clusterId},
              ${cluster.environment.name.cqlLiteral()},
              'ACTIVE',
              ${cluster.updatedAt.timestampLiteral()}
            )
            """.trimIndent(),
        )
        cqlExecutor.execute(
            """
            INSERT INTO $keyspace.managed_cluster_credentials
            (cluster_id, username_ciphertext, password_ciphertext, tls_enabled, ssl_settings_ciphertext, created_at, updated_at)
            VALUES (
              ${credential.clusterId},
              ${credential.usernameCiphertext.cqlNullableLiteral()},
              ${credential.passwordCiphertext.cqlNullableLiteral()},
              ${credential.tlsEnabled},
              ${credential.sslSettingsCiphertext.cqlNullableLiteral()},
              ${credential.createdAt.timestampLiteral()},
              ${credential.updatedAt.timestampLiteral()}
            )
            """.trimIndent(),
        )
        saveHealthSnapshot(healthSnapshot)
    }

    fun grantDbaToSuperAdmin(
        clusterId: UUID,
        grantedAt: Instant,
    ) {
        val keyspace = configProvider.getConfig().keyspace
        val assignmentId = UUID.nameUUIDFromBytes("phase-2-m4-dba-$clusterId".toByteArray(Charsets.UTF_8))

        cqlExecutor.execute(
            """
            INSERT INTO $keyspace.role_assignments
            (assignment_id, member_id, role_id, scope_type, scope_id, approval_source, granted_by, granted_at, effective_from, expires_at, status)
            VALUES (
              $assignmentId,
              ${MetadataSeedCatalog.SUPER_ADMIN_MEMBER_ID},
              ${MetadataSeedCatalog.DBA_ROLE_ID},
              'CLUSTER',
              ${clusterId.toString().cqlLiteral()},
              'BOOTSTRAP',
              'System',
              ${grantedAt.timestampLiteral()},
              ${grantedAt.timestampLiteral()},
              null,
              'ACTIVE'
            )
            """.trimIndent(),
        )
    }

    fun recordAuditEvent(
        eventType: String,
        targetType: String,
        targetId: String,
        details: Map<String, String>,
        createdAt: Instant,
    ) {
        val keyspace = configProvider.getConfig().keyspace
        val eventId = UUID.nameUUIDFromBytes("$eventType:$targetType:$targetId".toByteArray(Charsets.UTF_8))

        cqlExecutor.execute(
            """
            INSERT INTO $keyspace.audit_logs
            (event_id, event_type, actor, target_type, target_id, details, created_at)
            VALUES (
              $eventId,
              ${eventType.cqlLiteral()},
              'System',
              ${targetType.cqlLiteral()},
              ${targetId.cqlLiteral()},
              ${details.cqlMapLiteral()},
              ${createdAt.timestampLiteral()}
            )
            """.trimIndent(),
        )
    }

    private fun saveHealthSnapshot(healthSnapshot: ClusterHealthSnapshot) {
        val keyspace = configProvider.getConfig().keyspace

        cqlExecutor.execute(
            """
            INSERT INTO $keyspace.managed_cluster_health_snapshots
            (cluster_id, snapshot_id, status, node_count, up_node_count, schema_agreement, keyspace_count, table_count, pending_compactions, repairs_status, checked_at)
            VALUES (
              ${healthSnapshot.clusterId},
              ${healthSnapshot.snapshotId},
              ${healthSnapshot.status.name.cqlLiteral()},
              ${healthSnapshot.nodeCount},
              ${healthSnapshot.upNodeCount},
              ${healthSnapshot.schemaAgreement},
              ${healthSnapshot.keyspaceCount},
              ${healthSnapshot.tableCount},
              ${healthSnapshot.pendingCompactions ?: 0},
              ${healthSnapshot.repairsStatus.cqlLiteral()},
              ${healthSnapshot.checkedAt.timestampLiteral()}
            )
            """.trimIndent(),
        )
    }
}

private fun String.cqlLiteral(): String = "'${replace("'", "''")}'"

private fun String?.cqlNullableLiteral(): String = this?.cqlLiteral() ?: "null"

private fun Iterable<String>.cqlListLiteral(): String = joinToString(separator = ", ", prefix = "[", postfix = "]") { it.cqlLiteral() }

private fun Map<String, String>.cqlMapLiteral(): String =
    entries.joinToString(separator = ", ", prefix = "{", postfix = "}") { (key, value) ->
        "${key.cqlLiteral()}: ${value.cqlLiteral()}"
    }

private fun Instant.timestampLiteral(): String = toString().cqlLiteral()
