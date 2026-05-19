package kr.hakdang.cassdio.core.identity

import java.time.Instant
import java.util.UUID

enum class MemberStatus {
    PENDING,
    ACTIVE,
    SUSPENDED,
    DISABLED,
    DELETED,
}

enum class AuthProvider {
    LOCAL,
    OIDC,
    SAML,
}

enum class WorkspaceStatus {
    ACTIVE,
    ARCHIVED,
    DELETED,
}

enum class MembershipStatus {
    INVITED,
    ACTIVE,
    SUSPENDED,
    LEFT,
}

enum class RoleScopeType {
    APPLICATION,
    WORKSPACE,
    CLUSTER,
    KEYSPACE,
    TABLE,
    COLUMN,
}

enum class RoleAssignmentStatus {
    ACTIVE,
    REVOKED,
    EXPIRED,
}

data class Member(
    val memberId: UUID,
    val workspaceId: UUID?,
    val email: String,
    val displayName: String,
    val passwordHash: String?,
    val status: MemberStatus,
    val authProvider: AuthProvider,
    val mfaEnabled: Boolean,
    val locale: String,
    val timezone: String,
    val lastLoginAt: Instant?,
    val passwordChangedAt: Instant?,
    val createdAt: Instant,
    val updatedAt: Instant,
)

data class Workspace(
    val workspaceId: UUID,
    val name: String,
    val description: String?,
    val locale: String,
    val timezone: String,
    val signupMode: String,
    val ownerMemberId: UUID?,
    val status: WorkspaceStatus,
    val createdAt: Instant,
    val updatedAt: Instant,
)

data class WorkspaceMember(
    val workspaceId: UUID,
    val memberId: UUID,
    val membershipStatus: MembershipStatus,
    val defaultWorkspace: Boolean,
    val joinedAt: Instant?,
    val invitedBy: UUID?,
    val lastSelectedAt: Instant?,
)

data class Role(
    val roleId: UUID,
    val name: String,
    val description: String?,
    val systemRole: Boolean,
    val scopeType: RoleScopeType,
    val permissions: Set<String>,
    val createdAt: Instant,
    val updatedAt: Instant,
)

data class PermissionBinding(
    val bindingId: UUID,
    val roleId: UUID,
    val action: String,
    val resourcePattern: String,
    val effect: String,
    val condition: String?,
    val expiresAt: Instant?,
    val createdAt: Instant,
)

data class RoleAssignment(
    val assignmentId: UUID,
    val memberId: UUID,
    val roleId: UUID,
    val scopeType: RoleScopeType,
    val scopeId: String,
    val approvalSource: String,
    val grantedBy: String,
    val grantedAt: Instant,
    val effectiveFrom: Instant,
    val expiresAt: Instant?,
    val status: RoleAssignmentStatus,
)
