package kr.hakdang.cassdio.core.metadata.cluster

import kr.hakdang.cassdio.core.metadata.bootstrap.MetadataSeedCatalog
import kr.hakdang.cassdio.core.metadata.config.MetadataDbConfigProvider
import kr.hakdang.cassdio.core.metadata.cql.CqlExecutor
import kr.hakdang.cassdio.core.metadata.cql.CqlRow
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
            "SELECT cluster_id, status FROM $keyspace.managed_clusters_by_name WHERE name = ${name.cqlLiteral()}",
        ) != null
    }

    fun list(includeDeleted: Boolean = false): List<ManagedCluster> {
        val keyspace = configProvider.getConfig().keyspace
        return cqlExecutor
            .query("SELECT * FROM $keyspace.managed_clusters")
            .map { it.toManagedCluster() }
            .filter { includeDeleted || it.status != ManagedClusterStatus.DELETED }
            .sortedWith(compareBy<ManagedCluster> { it.environment.name }.thenBy { it.name })
    }

    fun findById(clusterId: UUID): ManagedCluster? {
        val keyspace = configProvider.getConfig().keyspace
        return cqlExecutor
            .queryOne("SELECT * FROM $keyspace.managed_clusters WHERE cluster_id = $clusterId")
            ?.toManagedCluster()
            ?.takeIf { it.status != ManagedClusterStatus.DELETED }
    }

    fun findCredential(clusterId: UUID): EncryptedClusterCredential? {
        val keyspace = configProvider.getConfig().keyspace
        return cqlExecutor
            .queryOne("SELECT * FROM $keyspace.managed_cluster_credentials WHERE cluster_id = $clusterId")
            ?.toEncryptedCredential()
    }

    fun latestHealthSnapshot(clusterId: UUID): ClusterHealthSnapshot? {
        val keyspace = configProvider.getConfig().keyspace
        return cqlExecutor
            .query("SELECT * FROM $keyspace.managed_cluster_health_snapshots WHERE cluster_id = $clusterId")
            .map { it.toHealthSnapshot() }
            .maxByOrNull { it.checkedAt }
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
              ${cluster.status.name.cqlLiteral()}
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
              ${cluster.status.name.cqlLiteral()},
              ${cluster.updatedAt.timestampLiteral()}
            )
            """.trimIndent(),
        )
        cqlExecutor.execute(
            """
            INSERT INTO $keyspace.managed_cluster_credentials
            (cluster_id, username_ciphertext, password_ciphertext, secret_reference_ciphertext, tls_enabled, ssl_settings_ciphertext, created_at, updated_at)
            VALUES (
              ${credential.clusterId},
              ${credential.usernameCiphertext.cqlNullableLiteral()},
              ${credential.passwordCiphertext.cqlNullableLiteral()},
              ${credential.secretReferenceCiphertext.cqlNullableLiteral()},
              ${credential.tlsEnabled},
              ${credential.sslSettingsCiphertext.cqlNullableLiteral()},
              ${credential.createdAt.timestampLiteral()},
              ${credential.updatedAt.timestampLiteral()}
            )
            """.trimIndent(),
        )
        saveHealthSnapshot(healthSnapshot)
    }

    fun update(cluster: ManagedCluster) {
        val keyspace = configProvider.getConfig().keyspace

        cqlExecutor.execute(
            """
            UPDATE $keyspace.managed_clusters SET
              name = ${cluster.name.cqlLiteral()},
              environment = ${cluster.environment.name.cqlLiteral()},
              contact_points = ${cluster.contactPoints.cqlListLiteral()},
              port = ${cluster.port},
              local_datacenter = ${cluster.localDatacenter.cqlLiteral()},
              cassandra_version = ${cluster.cassandraVersion.cqlLiteral()},
              keyspace_count = ${cluster.keyspaceCount},
              table_count = ${cluster.tableCount},
              owner_member_id = ${cluster.ownerMemberId},
              updated_at = ${cluster.updatedAt.timestampLiteral()},
              status = ${cluster.status.name.cqlLiteral()}
            WHERE cluster_id = ${cluster.clusterId}
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
              ${cluster.status.name.cqlLiteral()},
              ${cluster.updatedAt.timestampLiteral()}
            )
            """.trimIndent(),
        )
    }

    fun updateCredential(credential: EncryptedClusterCredential) {
        val keyspace = configProvider.getConfig().keyspace
        cqlExecutor.execute(
            """
            UPDATE $keyspace.managed_cluster_credentials SET
              username_ciphertext = ${credential.usernameCiphertext.cqlNullableLiteral()},
              password_ciphertext = ${credential.passwordCiphertext.cqlNullableLiteral()},
              secret_reference_ciphertext = ${credential.secretReferenceCiphertext.cqlNullableLiteral()},
              tls_enabled = ${credential.tlsEnabled},
              ssl_settings_ciphertext = ${credential.sslSettingsCiphertext.cqlNullableLiteral()},
              updated_at = ${credential.updatedAt.timestampLiteral()}
            WHERE cluster_id = ${credential.clusterId}
            """.trimIndent(),
        )
    }

    fun markDeleted(
        cluster: ManagedCluster,
        deletedAt: Instant,
    ) {
        update(cluster.copy(status = ManagedClusterStatus.DELETED, updatedAt = deletedAt))
    }

    fun saveHealth(healthSnapshot: ClusterHealthSnapshot) {
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

internal fun CqlRow.toManagedCluster(): ManagedCluster =
    ManagedCluster(
        clusterId = requireNotNull(uuid("cluster_id")),
        name = requireNotNull(string("name")),
        environment = kr.hakdang.cassdio.core.metadata.config.ManagedClusterEnvironment.valueOf(string("environment") ?: "DEV"),
        contactPoints = stringList("contact_points").orEmpty(),
        port = int("port") ?: 9042,
        localDatacenter = string("local_datacenter") ?: "datacenter1",
        cassandraVersion = string("cassandra_version") ?: "unknown",
        keyspaceCount = int("keyspace_count") ?: 0,
        tableCount = int("table_count") ?: 0,
        ownerMemberId = requireNotNull(uuid("owner_member_id")),
        createdAt = instant("created_at") ?: Instant.EPOCH,
        updatedAt = instant("updated_at") ?: Instant.EPOCH,
        status = ManagedClusterStatus.valueOf(string("status") ?: ManagedClusterStatus.ACTIVE.name),
    )

internal fun CqlRow.toEncryptedCredential(): EncryptedClusterCredential =
    EncryptedClusterCredential(
        clusterId = requireNotNull(uuid("cluster_id")),
        usernameCiphertext = string("username_ciphertext"),
        passwordCiphertext = string("password_ciphertext"),
        secretReferenceCiphertext = string("secret_reference_ciphertext"),
        tlsEnabled = boolean("tls_enabled") ?: false,
        sslSettingsCiphertext = string("ssl_settings_ciphertext"),
        createdAt = instant("created_at") ?: Instant.EPOCH,
        updatedAt = instant("updated_at") ?: Instant.EPOCH,
    )

internal fun CqlRow.toHealthSnapshot(): ClusterHealthSnapshot =
    ClusterHealthSnapshot(
        snapshotId = requireNotNull(uuid("snapshot_id")),
        clusterId = requireNotNull(uuid("cluster_id")),
        status = ClusterHealthStatus.valueOf(string("status") ?: ClusterHealthStatus.UNHEALTHY.name),
        nodeCount = int("node_count") ?: 0,
        upNodeCount = int("up_node_count") ?: 0,
        schemaAgreement = boolean("schema_agreement") ?: false,
        keyspaceCount = int("keyspace_count") ?: 0,
        tableCount = int("table_count") ?: 0,
        pendingCompactions = int("pending_compactions"),
        repairsStatus = string("repairs_status") ?: "UNKNOWN",
        checkedAt = instant("checked_at") ?: Instant.EPOCH,
    )

private fun String.cqlLiteral(): String = "'${replace("'", "''")}'"

private fun String?.cqlNullableLiteral(): String = this?.cqlLiteral() ?: "null"

private fun Iterable<String>.cqlListLiteral(): String = joinToString(separator = ", ", prefix = "[", postfix = "]") { it.cqlLiteral() }

private fun Map<String, String>.cqlMapLiteral(): String =
    entries.joinToString(separator = ", ", prefix = "{", postfix = "}") { (key, value) ->
        "${key.cqlLiteral()}: ${value.cqlLiteral()}"
    }

private fun Instant.timestampLiteral(): String = toString().cqlLiteral()
