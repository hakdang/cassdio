package kr.hakdang.cassdio.core.metadata.bootstrap

import kr.hakdang.cassdio.core.metadata.config.MetadataDbConfigProvider
import org.springframework.stereotype.Component

@Component
class MetadataMigrationCatalog(
    private val configProvider: MetadataDbConfigProvider,
) {
    fun migrations(): List<MetadataMigration> {
        val keyspace = configProvider.getConfig().keyspace

        return listOf(
            MetadataMigration(
                version = "202602010001",
                description = "Create metadata bootstrap schema",
                statements =
                    listOf(
                        """
                        CREATE TABLE IF NOT EXISTS $keyspace.schema_migrations (
                          version TEXT PRIMARY KEY,
                          description TEXT,
                          executed_at TIMESTAMP,
                          execution_time INT,
                          success BOOLEAN
                        )
                        """.trimIndent(),
                        """
                        CREATE TABLE IF NOT EXISTS $keyspace.bootstrap_locks (
                          name TEXT PRIMARY KEY,
                          owner TEXT,
                          status TEXT,
                          heartbeat_at TIMESTAMP,
                          expires_at TIMESTAMP
                        )
                        """.trimIndent(),
                        """
                        CREATE TABLE IF NOT EXISTS $keyspace.installation_state (
                          id TEXT PRIMARY KEY,
                          installation_id UUID,
                          bootstrap_completed_at TIMESTAMP,
                          schema_version TEXT,
                          cassdio_version TEXT,
                          initial_settings MAP<TEXT, TEXT>,
                          updated_at TIMESTAMP
                        )
                        """.trimIndent(),
                        """
                        CREATE TABLE IF NOT EXISTS $keyspace.seed_history (
                          idempotency_key TEXT PRIMARY KEY,
                          description TEXT,
                          executed_at TIMESTAMP,
                          success BOOLEAN
                        )
                        """.trimIndent(),
                    ),
            ),
            MetadataMigration(
                version = "202605190001",
                description = "Create initial metadata seed schema",
                statements =
                    listOf(
                        """
                        CREATE TABLE IF NOT EXISTS $keyspace.workspaces (
                          workspace_id UUID PRIMARY KEY,
                          name TEXT,
                          description TEXT,
                          locale TEXT,
                          timezone TEXT,
                          signup_mode TEXT,
                          owner_member_id UUID,
                          status TEXT,
                          created_at TIMESTAMP,
                          updated_at TIMESTAMP
                        )
                        """.trimIndent(),
                        """
                        CREATE TABLE IF NOT EXISTS $keyspace.members (
                          member_id UUID PRIMARY KEY,
                          workspace_id UUID,
                          display_name TEXT,
                          email TEXT,
                          email_verified BOOLEAN,
                          password_hash TEXT,
                          password_algorithm TEXT,
                          mfa_status TEXT,
                          status TEXT,
                          auth_provider TEXT,
                          created_at TIMESTAMP,
                          updated_at TIMESTAMP
                        )
                        """.trimIndent(),
                        """
                        CREATE TABLE IF NOT EXISTS $keyspace.roles (
                          role_id UUID PRIMARY KEY,
                          name TEXT,
                          description TEXT,
                          system_role BOOLEAN,
                          scope_type TEXT,
                          permissions SET<TEXT>,
                          created_at TIMESTAMP,
                          updated_at TIMESTAMP
                        )
                        """.trimIndent(),
                        """
                        CREATE TABLE IF NOT EXISTS $keyspace.role_assignments (
                          assignment_id UUID PRIMARY KEY,
                          member_id UUID,
                          role_id UUID,
                          scope_type TEXT,
                          scope_id TEXT,
                          approval_source TEXT,
                          granted_by TEXT,
                          granted_at TIMESTAMP,
                          effective_from TIMESTAMP,
                          expires_at TIMESTAMP,
                          status TEXT
                        )
                        """.trimIndent(),
                        """
                        CREATE TABLE IF NOT EXISTS $keyspace.query_policies (
                          policy_id UUID PRIMARY KEY,
                          name TEXT,
                          query_type TEXT,
                          description TEXT,
                          rules MAP<TEXT, TEXT>,
                          examples LIST<TEXT>,
                          enabled BOOLEAN,
                          priority INT,
                          created_at TIMESTAMP,
                          updated_at TIMESTAMP
                        )
                        """.trimIndent(),
                        """
                        CREATE TABLE IF NOT EXISTS $keyspace.workflow_policies (
                          policy_id UUID PRIMARY KEY,
                          name TEXT,
                          workflow_type TEXT,
                          description TEXT,
                          approval_steps LIST<TEXT>,
                          priority INT,
                          enabled BOOLEAN,
                          created_at TIMESTAMP,
                          updated_at TIMESTAMP
                        )
                        """.trimIndent(),
                        """
                        CREATE TABLE IF NOT EXISTS $keyspace.audit_logs (
                          event_id UUID PRIMARY KEY,
                          event_type TEXT,
                          actor TEXT,
                          target_type TEXT,
                          target_id TEXT,
                          details MAP<TEXT, TEXT>,
                          created_at TIMESTAMP
                        )
                        """.trimIndent(),
                    ),
            ),
            MetadataMigration(
                version = "202605190002",
                description = "Create managed cluster registration schema",
                statements =
                    listOf(
                        """
                        CREATE TABLE IF NOT EXISTS $keyspace.managed_clusters (
                          cluster_id UUID PRIMARY KEY,
                          name TEXT,
                          environment TEXT,
                          contact_points LIST<TEXT>,
                          port INT,
                          local_datacenter TEXT,
                          cassandra_version TEXT,
                          keyspace_count INT,
                          table_count INT,
                          owner_member_id UUID,
                          created_at TIMESTAMP,
                          updated_at TIMESTAMP,
                          status TEXT
                        )
                        """.trimIndent(),
                        """
                        CREATE TABLE IF NOT EXISTS $keyspace.managed_clusters_by_name (
                          name TEXT PRIMARY KEY,
                          cluster_id UUID,
                          environment TEXT,
                          status TEXT,
                          updated_at TIMESTAMP
                        )
                        """.trimIndent(),
                        """
                        CREATE TABLE IF NOT EXISTS $keyspace.managed_cluster_credentials (
                          cluster_id UUID PRIMARY KEY,
                          username_ciphertext TEXT,
                          password_ciphertext TEXT,
                          tls_enabled BOOLEAN,
                          ssl_settings_ciphertext TEXT,
                          created_at TIMESTAMP,
                          updated_at TIMESTAMP
                        )
                        """.trimIndent(),
                        """
                        CREATE TABLE IF NOT EXISTS $keyspace.managed_cluster_health_snapshots (
                          cluster_id UUID,
                          snapshot_id UUID,
                          status TEXT,
                          node_count INT,
                          up_node_count INT,
                          schema_agreement BOOLEAN,
                          keyspace_count INT,
                          table_count INT,
                          pending_compactions INT,
                          repairs_status TEXT,
                          checked_at TIMESTAMP,
                          PRIMARY KEY (cluster_id, snapshot_id)
                        )
                        """.trimIndent(),
                    ),
            ),
        )
    }
}
