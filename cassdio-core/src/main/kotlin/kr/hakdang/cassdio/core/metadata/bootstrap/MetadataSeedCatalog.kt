package kr.hakdang.cassdio.core.metadata.bootstrap

import kr.hakdang.cassdio.core.metadata.config.MetadataBootstrapProperties
import kr.hakdang.cassdio.core.metadata.config.MetadataDbConfigProvider
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.Instant
import java.util.UUID

@Component
open class MetadataSeedCatalog(
    private val configProvider: MetadataDbConfigProvider? = null,
    private val bootstrapProperties: MetadataBootstrapProperties = MetadataBootstrapProperties(),
    private val passwordHashService: PasswordHashService = BCryptPasswordHashService(),
    private val clock: Clock = Clock.systemUTC(),
) {
    open fun seeds(): List<SeedDefinition> {
        val keyspace = configProvider?.getConfig()?.keyspace ?: return emptyList()
        val now = Instant.now(clock)
        val seedProperties = bootstrapProperties.seed
        require(seedProperties.superAdmin.email.contains("@")) { "Super admin email must be valid." }
        val passwordHash = passwordHashService.hash(seedProperties.superAdmin.initialPassword)

        return listOf(
            defaultWorkspaceSeed(keyspace, now),
            superAdminMemberSeed(keyspace, now, passwordHash),
            systemRolesSeed(keyspace, now),
            superAdminAssignmentSeed(keyspace, now),
            queryPoliciesSeed(keyspace, now),
            workflowPoliciesSeed(keyspace, now),
            bootstrapAuditSeed(keyspace, now),
        )
    }

    private fun defaultWorkspaceSeed(
        keyspace: String,
        now: Instant,
    ): SeedDefinition {
        val workspace = bootstrapProperties.seed.workspace

        return SeedDefinition(
            idempotencyKey = "phase-2-m3-default-workspace",
            description = "Create default workspace",
            statements =
                listOf(
                    """
                    INSERT INTO $keyspace.workspaces
                    (workspace_id, name, description, locale, timezone, signup_mode, owner_member_id, status, created_at, updated_at)
                    VALUES (
                      $DEFAULT_WORKSPACE_ID,
                      ${workspace.name.cqlLiteral()},
                      ${workspace.description.cqlLiteral()},
                      ${workspace.locale.cqlLiteral()},
                      ${workspace.timezone.cqlLiteral()},
                      ${workspace.signupMode.cqlLiteral()},
                      $SUPER_ADMIN_MEMBER_ID,
                      'ACTIVE',
                      ${now.timestampLiteral()},
                      ${now.timestampLiteral()}
                    )
                    """.trimIndent(),
                ),
        )
    }

    private fun superAdminMemberSeed(
        keyspace: String,
        now: Instant,
        passwordHash: String,
    ): SeedDefinition {
        val superAdmin = bootstrapProperties.seed.superAdmin

        return SeedDefinition(
            idempotencyKey = "phase-2-m3-super-admin-member",
            description = "Create initial super admin member",
            statements =
                listOf(
                    """
                    INSERT INTO $keyspace.members
                    (member_id, workspace_id, display_name, email, email_verified, password_hash, password_algorithm, mfa_status, status, auth_provider, created_at, updated_at)
                    VALUES (
                      $SUPER_ADMIN_MEMBER_ID,
                      $DEFAULT_WORKSPACE_ID,
                      ${superAdmin.displayName.cqlLiteral()},
                      ${superAdmin.email.cqlLiteral()},
                      true,
                      ${passwordHash.cqlLiteral()},
                      'BCRYPT',
                      'PLACEHOLDER',
                      'ACTIVE',
                      'LOCAL',
                      ${now.timestampLiteral()},
                      ${now.timestampLiteral()}
                    )
                    """.trimIndent(),
                ),
        )
    }

    private fun systemRolesSeed(
        keyspace: String,
        now: Instant,
    ): SeedDefinition =
        SeedDefinition(
            idempotencyKey = "phase-2-m3-system-roles",
            description = "Create default system roles",
            statements =
                systemRoles().map { role ->
                    """
                    INSERT INTO $keyspace.roles
                    (role_id, name, description, system_role, scope_type, permissions, created_at, updated_at)
                    VALUES (
                      ${role.id},
                      ${role.name.cqlLiteral()},
                      ${role.description.cqlLiteral()},
                      true,
                      ${role.scopeType.cqlLiteral()},
                      ${role.permissions.cqlSetLiteral()},
                      ${now.timestampLiteral()},
                      ${now.timestampLiteral()}
                    )
                    """.trimIndent()
                },
        )

    private fun superAdminAssignmentSeed(
        keyspace: String,
        now: Instant,
    ): SeedDefinition =
        SeedDefinition(
            idempotencyKey = "phase-2-m3-super-admin-role-assignment",
            description = "Assign super admin role to initial member",
            statements =
                listOf(
                    """
                    INSERT INTO $keyspace.role_assignments
                    (assignment_id, member_id, role_id, scope_type, scope_id, approval_source, granted_by, granted_at, effective_from, expires_at, status)
                    VALUES (
                      $SUPER_ADMIN_ASSIGNMENT_ID,
                      $SUPER_ADMIN_MEMBER_ID,
                      $SUPER_ADMIN_ROLE_ID,
                      'APPLICATION',
                      'cassdio',
                      'BOOTSTRAP',
                      'System',
                      ${now.timestampLiteral()},
                      ${now.timestampLiteral()},
                      null,
                      'ACTIVE'
                    )
                    """.trimIndent(),
                ),
        )

    private fun queryPoliciesSeed(
        keyspace: String,
        now: Instant,
    ): SeedDefinition =
        SeedDefinition(
            idempotencyKey = "phase-2-m3-default-query-policies",
            description = "Create conservative default query policies",
            statements =
                queryPolicies().mapIndexed { index, policy ->
                    """
                    INSERT INTO $keyspace.query_policies
                    (policy_id, name, query_type, description, rules, examples, enabled, priority, created_at, updated_at)
                    VALUES (
                      ${policy.id},
                      ${policy.name.cqlLiteral()},
                      ${policy.queryType.cqlLiteral()},
                      ${policy.description.cqlLiteral()},
                      ${policy.rules.cqlMapLiteral()},
                      ${policy.examples.cqlListLiteral()},
                      true,
                      ${index + 10},
                      ${now.timestampLiteral()},
                      ${now.timestampLiteral()}
                    )
                    """.trimIndent()
                },
        )

    private fun workflowPoliciesSeed(
        keyspace: String,
        now: Instant,
    ): SeedDefinition =
        SeedDefinition(
            idempotencyKey = "phase-2-m3-default-workflow-policies",
            description = "Create default workflow approval policies",
            statements =
                workflowPolicies().mapIndexed { index, policy ->
                    """
                    INSERT INTO $keyspace.workflow_policies
                    (policy_id, name, workflow_type, description, approval_steps, priority, enabled, created_at, updated_at)
                    VALUES (
                      ${policy.id},
                      ${policy.name.cqlLiteral()},
                      ${policy.workflowType.cqlLiteral()},
                      ${policy.description.cqlLiteral()},
                      ${policy.approvalSteps.cqlListLiteral()},
                      ${index + 10},
                      true,
                      ${now.timestampLiteral()},
                      ${now.timestampLiteral()}
                    )
                    """.trimIndent()
                },
        )

    private fun bootstrapAuditSeed(
        keyspace: String,
        now: Instant,
    ): SeedDefinition =
        SeedDefinition(
            idempotencyKey = "phase-2-m3-bootstrap-audit-log",
            description = "Record bootstrap seed audit events",
            statements =
                bootstrapAuditEvents().map { event ->
                    """
                    INSERT INTO $keyspace.audit_logs
                    (event_id, event_type, actor, target_type, target_id, details, created_at)
                    VALUES (
                      ${event.id},
                      ${event.eventType.cqlLiteral()},
                      'System',
                      ${event.targetType.cqlLiteral()},
                      ${event.targetId.cqlLiteral()},
                      ${event.details.cqlMapLiteral()},
                      ${now.timestampLiteral()}
                    )
                    """.trimIndent()
                },
        )

    private fun systemRoles(): List<RoleSeed> =
        listOf(
            RoleSeed(
                id = SUPER_ADMIN_ROLE_ID,
                name = "Super Admin",
                description = "Full application administration access.",
                scopeType = "APPLICATION",
                permissions = listOf("*"),
            ),
            RoleSeed(
                id = DBA_ROLE_ID,
                name = "DBA",
                description = "Cluster, schema, and DDL administration.",
                scopeType = "WORKSPACE",
                permissions = listOf("cluster:*", "schema:*", "query:ddl:*", "workflow:review:dba"),
            ),
            RoleSeed(
                id = SECURITY_ADMIN_ROLE_ID,
                name = "Security Admin",
                description = "Security policy, masking, and permission administration.",
                scopeType = "WORKSPACE",
                permissions = listOf("security:*", "policy:*", "permission:*", "workflow:review:security"),
            ),
            RoleSeed(
                id = DATA_READER_ROLE_ID,
                name = "Data Reader",
                description = "Read-only data access through approved SELECT queries.",
                scopeType = "WORKSPACE",
                permissions = listOf("query:select", "schema:read", "catalog:read"),
            ),
            RoleSeed(
                id = DATA_ANALYST_ROLE_ID,
                name = "Data Analyst",
                description = "Analyst access with limited DML request capability.",
                scopeType = "WORKSPACE",
                permissions = listOf("query:select", "query:dml:request", "export:request", "schema:read", "catalog:read"),
            ),
        )

    private fun queryPolicies(): List<QueryPolicySeed> =
        listOf(
            QueryPolicySeed(
                id = SELECT_POLICY_ID,
                name = "Default SELECT Policy",
                queryType = "SELECT",
                description = "SELECT queries require an explicit LIMIT and are capped to a conservative row count.",
                rules = mapOf("limit.required" to "true", "limit.max_rows" to "1000", "allow_filtering" to "warn"),
                examples = listOf("SELECT * FROM keyspace.table WHERE id = ? LIMIT 1000"),
            ),
            QueryPolicySeed(
                id = DML_POLICY_ID,
                name = "Default DML Policy",
                queryType = "DML",
                description = "DML statements require a WHERE clause, row estimate, and approval on risky scopes.",
                rules = mapOf("where.required" to "true", "max_estimated_rows" to "1000", "approval.required" to "risk_based"),
                examples = listOf("UPDATE keyspace.table SET value = ? WHERE id = ?"),
            ),
            QueryPolicySeed(
                id = DDL_POLICY_ID,
                name = "Default DDL Policy",
                queryType = "DDL",
                description = "DDL statements require workflow approval before execution.",
                rules = mapOf("approval.required" to "true", "reviewers" to "DBA,Security Admin", "execution.requires_token" to "true"),
                examples = listOf("CREATE TABLE keyspace.table (...)"),
            ),
            QueryPolicySeed(
                id = ADMIN_CQL_POLICY_ID,
                name = "Default Admin CQL Policy",
                queryType = "ADMIN_CQL",
                description = "Administrative CQL is blocked until an explicit policy overrides it.",
                rules = mapOf("blocked" to "true", "override.required" to "true"),
                examples = listOf("TRUNCATE keyspace.table"),
            ),
        )

    private fun workflowPolicies(): List<WorkflowPolicySeed> =
        listOf(
            WorkflowPolicySeed(
                id = SIGNUP_WORKFLOW_POLICY_ID,
                name = "Signup Approval",
                workflowType = "SIGNUP",
                description = "New members require workspace administrator approval.",
                approvalSteps = listOf("Workspace Admin"),
            ),
            WorkflowPolicySeed(
                id = PERMISSION_WORKFLOW_POLICY_ID,
                name = "Permission Request Approval",
                workflowType = "PERMISSION_REQUEST",
                description = "Role and resource permission requests require owner approval.",
                approvalSteps = listOf("Resource Owner", "Workspace Admin"),
            ),
            WorkflowPolicySeed(
                id = PROD_DML_WORKFLOW_POLICY_ID,
                name = "Production DML Approval",
                workflowType = "DML",
                description = "Production DML requires DBA approval.",
                approvalSteps = listOf("DBA"),
            ),
            WorkflowPolicySeed(
                id = DDL_WORKFLOW_POLICY_ID,
                name = "DDL Approval",
                workflowType = "DDL",
                description = "DDL changes require DBA and security approval.",
                approvalSteps = listOf("DBA", "Security Reviewer"),
            ),
            WorkflowPolicySeed(
                id = TABLE_CREATION_WORKFLOW_POLICY_ID,
                name = "Table Creation Approval",
                workflowType = "TABLE_CREATION",
                description = "Table creation requires keyspace owner and DBA approval.",
                approvalSteps = listOf("Keyspace Owner", "DBA"),
            ),
        )

    private fun bootstrapAuditEvents(): List<AuditEventSeed> =
        listOf(
            AuditEventSeed(BOOTSTRAP_STARTED_EVENT_ID, "BOOTSTRAP_STARTED", "bootstrap", "metadata-bootstrap"),
            AuditEventSeed(
                KEYSPACE_CREATED_EVENT_ID,
                "METADATA_KEYSPACE_CREATED",
                "keyspace",
                configProvider?.getConfig()?.keyspace ?: "unknown",
            ),
            AuditEventSeed(SUPER_ADMIN_CREATED_EVENT_ID, "SUPER_ADMIN_CREATED", "member", SUPER_ADMIN_MEMBER_ID.toString()),
            AuditEventSeed(SYSTEM_ROLES_CREATED_EVENT_ID, "SYSTEM_ROLES_CREATED", "role", "system"),
            AuditEventSeed(DEFAULT_POLICIES_CREATED_EVENT_ID, "DEFAULT_POLICIES_CREATED", "policy", "default"),
            AuditEventSeed(BOOTSTRAP_COMPLETED_EVENT_ID, "BOOTSTRAP_COMPLETED", "bootstrap", "metadata-bootstrap"),
        )

    private data class RoleSeed(
        val id: UUID,
        val name: String,
        val description: String,
        val scopeType: String,
        val permissions: List<String>,
    )

    private data class QueryPolicySeed(
        val id: UUID,
        val name: String,
        val queryType: String,
        val description: String,
        val rules: Map<String, String>,
        val examples: List<String>,
    )

    private data class WorkflowPolicySeed(
        val id: UUID,
        val name: String,
        val workflowType: String,
        val description: String,
        val approvalSteps: List<String>,
    )

    private data class AuditEventSeed(
        val id: UUID,
        val eventType: String,
        val targetType: String,
        val targetId: String,
        val details: Map<String, String> = mapOf("phase" to "2", "milestone" to "3"),
    )

    companion object {
        val DEFAULT_WORKSPACE_ID: UUID = UUID.fromString("00000000-0000-2003-8000-000000000001")
        val SUPER_ADMIN_MEMBER_ID: UUID = UUID.fromString("00000000-0000-2003-8000-000000000002")
        val SUPER_ADMIN_ROLE_ID: UUID = UUID.fromString("00000000-0000-2003-8000-000000000101")
        val DBA_ROLE_ID: UUID = UUID.fromString("00000000-0000-2003-8000-000000000102")
        val SECURITY_ADMIN_ROLE_ID: UUID = UUID.fromString("00000000-0000-2003-8000-000000000103")
        val DATA_READER_ROLE_ID: UUID = UUID.fromString("00000000-0000-2003-8000-000000000104")
        val DATA_ANALYST_ROLE_ID: UUID = UUID.fromString("00000000-0000-2003-8000-000000000105")
        val SUPER_ADMIN_ASSIGNMENT_ID: UUID = UUID.fromString("00000000-0000-2003-8000-000000000201")
        val SELECT_POLICY_ID: UUID = UUID.fromString("00000000-0000-2003-8000-000000000301")
        val DML_POLICY_ID: UUID = UUID.fromString("00000000-0000-2003-8000-000000000302")
        val DDL_POLICY_ID: UUID = UUID.fromString("00000000-0000-2003-8000-000000000303")
        val ADMIN_CQL_POLICY_ID: UUID = UUID.fromString("00000000-0000-2003-8000-000000000304")
        val SIGNUP_WORKFLOW_POLICY_ID: UUID = UUID.fromString("00000000-0000-2003-8000-000000000401")
        val PERMISSION_WORKFLOW_POLICY_ID: UUID = UUID.fromString("00000000-0000-2003-8000-000000000402")
        val PROD_DML_WORKFLOW_POLICY_ID: UUID = UUID.fromString("00000000-0000-2003-8000-000000000403")
        val DDL_WORKFLOW_POLICY_ID: UUID = UUID.fromString("00000000-0000-2003-8000-000000000404")
        val TABLE_CREATION_WORKFLOW_POLICY_ID: UUID = UUID.fromString("00000000-0000-2003-8000-000000000405")
        val BOOTSTRAP_STARTED_EVENT_ID: UUID = UUID.fromString("00000000-0000-2003-8000-000000000501")
        val KEYSPACE_CREATED_EVENT_ID: UUID = UUID.fromString("00000000-0000-2003-8000-000000000502")
        val SUPER_ADMIN_CREATED_EVENT_ID: UUID = UUID.fromString("00000000-0000-2003-8000-000000000503")
        val SYSTEM_ROLES_CREATED_EVENT_ID: UUID = UUID.fromString("00000000-0000-2003-8000-000000000504")
        val DEFAULT_POLICIES_CREATED_EVENT_ID: UUID = UUID.fromString("00000000-0000-2003-8000-000000000505")
        val BOOTSTRAP_COMPLETED_EVENT_ID: UUID = UUID.fromString("00000000-0000-2003-8000-000000000506")
    }
}

interface PasswordHashService {
    fun hash(rawPassword: String): String
}

class BCryptPasswordHashService(
    private val encoder: BCryptPasswordEncoder = BCryptPasswordEncoder(),
) : PasswordHashService {
    override fun hash(rawPassword: String): String = encoder.encode(rawPassword)
}
