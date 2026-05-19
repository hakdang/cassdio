package kr.hakdang.cassdio.core.identity

import kr.hakdang.cassdio.core.metadata.config.MetadataDbConfigProvider
import kr.hakdang.cassdio.core.metadata.cql.CqlExecutor
import kr.hakdang.cassdio.core.metadata.cql.CqlRow
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID

@Repository
class RoleRepository(
    private val configProvider: MetadataDbConfigProvider,
    private val cqlExecutor: CqlExecutor,
) {
    fun list(): List<Role> {
        val keyspace = configProvider.getConfig().keyspace
        return cqlExecutor.query("SELECT * FROM $keyspace.roles").map { it.toRole() }
    }

    fun findById(roleId: UUID): Role? {
        val keyspace = configProvider.getConfig().keyspace
        return cqlExecutor.queryOne("SELECT * FROM $keyspace.roles WHERE role_id = $roleId")?.toRole()
    }

    fun save(role: Role) {
        val keyspace = configProvider.getConfig().keyspace
        cqlExecutor.execute(
            """
            INSERT INTO $keyspace.roles
            (role_id, name, description, system_role, scope_type, permissions, created_at, updated_at)
            VALUES (
              ${role.roleId},
              ${role.name.cqlLiteral()},
              ${role.description.cqlNullableLiteral()},
              ${role.systemRole},
              ${role.scopeType.name.cqlLiteral()},
              ${role.permissions.cqlSetLiteral()},
              ${role.createdAt.timestampLiteral()},
              ${role.updatedAt.timestampLiteral()}
            )
            """.trimIndent(),
        )
    }

    fun saveAssignment(assignment: RoleAssignment) {
        val keyspace = configProvider.getConfig().keyspace
        cqlExecutor.execute(
            """
            INSERT INTO $keyspace.role_assignments
            (assignment_id, member_id, role_id, scope_type, scope_id, approval_source, granted_by, granted_at, effective_from, expires_at, status)
            VALUES (
              ${assignment.assignmentId},
              ${assignment.memberId},
              ${assignment.roleId},
              ${assignment.scopeType.name.cqlLiteral()},
              ${assignment.scopeId.cqlLiteral()},
              ${assignment.approvalSource.cqlLiteral()},
              ${assignment.grantedBy.cqlLiteral()},
              ${assignment.grantedAt.timestampLiteral()},
              ${assignment.effectiveFrom.timestampLiteral()},
              ${assignment.expiresAt?.timestampLiteral() ?: "null"},
              ${assignment.status.name.cqlLiteral()}
            )
            """.trimIndent(),
        )
    }

    fun assignmentsForMember(memberId: UUID): List<RoleAssignment> {
        val keyspace = configProvider.getConfig().keyspace
        return cqlExecutor.query("SELECT * FROM $keyspace.role_assignments WHERE member_id = $memberId ALLOW FILTERING").map {
            it.toRoleAssignment()
        }
    }
}

private fun CqlRow.toRole(): Role =
    Role(
        roleId = requireNotNull(uuid("role_id")),
        name = requireNotNull(string("name")),
        description = string("description"),
        systemRole = boolean("system_role") == true,
        scopeType = RoleScopeType.valueOf(string("scope_type") ?: RoleScopeType.WORKSPACE.name),
        permissions = stringSet("permissions") ?: stringList("permissions")?.toSet() ?: emptySet(),
        createdAt = instant("created_at") ?: Instant.EPOCH,
        updatedAt = instant("updated_at") ?: Instant.EPOCH,
    )

private fun CqlRow.toRoleAssignment(): RoleAssignment =
    RoleAssignment(
        assignmentId = requireNotNull(uuid("assignment_id")),
        memberId = requireNotNull(uuid("member_id")),
        roleId = requireNotNull(uuid("role_id")),
        scopeType = RoleScopeType.valueOf(string("scope_type") ?: RoleScopeType.WORKSPACE.name),
        scopeId = string("scope_id") ?: "",
        approvalSource = string("approval_source") ?: "MANUAL",
        grantedBy = string("granted_by") ?: "System",
        grantedAt = instant("granted_at") ?: Instant.EPOCH,
        effectiveFrom = instant("effective_from") ?: Instant.EPOCH,
        expiresAt = instant("expires_at"),
        status = RoleAssignmentStatus.valueOf(string("status") ?: RoleAssignmentStatus.ACTIVE.name),
    )
