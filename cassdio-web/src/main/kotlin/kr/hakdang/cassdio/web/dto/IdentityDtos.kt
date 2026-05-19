package kr.hakdang.cassdio.web.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import kr.hakdang.cassdio.core.identity.Member
import kr.hakdang.cassdio.core.identity.MemberSession
import kr.hakdang.cassdio.core.identity.PermissionBinding
import kr.hakdang.cassdio.core.identity.Role
import kr.hakdang.cassdio.core.identity.RoleAssignment
import kr.hakdang.cassdio.core.identity.Workspace
import java.time.Instant
import java.util.UUID

data class LoginRequest(
    @field:Email val email: String,
    @field:NotBlank val password: String,
)

data class LoginResponse(
    val accessToken: String,
    val member: MemberResponse,
    val sessionId: UUID,
)

data class MemberResponse(
    val memberId: UUID,
    val email: String,
    val displayName: String,
    val status: String,
    val authProvider: String,
    val mfaEnabled: Boolean,
    val locale: String,
    val timezone: String,
    val lastLoginAt: Instant?,
    val createdAt: Instant,
)

data class CreateMemberRequest(
    @field:Email val email: String,
    @field:NotBlank val displayName: String,
    val password: String? = null,
    val workspaceId: UUID? = null,
    val locale: String = "ko-KR",
    val timezone: String = "Asia/Seoul",
)

data class UpdateMemberStatusRequest(
    @field:NotBlank val status: String,
)

data class WorkspaceResponse(
    val workspaceId: UUID,
    val name: String,
    val description: String?,
    val locale: String,
    val timezone: String,
    val signupMode: String,
    val ownerMemberId: UUID?,
    val status: String,
    val createdAt: Instant,
)

data class CreateWorkspaceRequest(
    @field:NotBlank val name: String,
    val description: String? = null,
    val locale: String = "ko-KR",
    val timezone: String = "Asia/Seoul",
    val signupMode: String = "MANUAL_APPROVAL",
    val ownerMemberId: UUID? = null,
)

data class RoleResponse(
    val roleId: UUID,
    val name: String,
    val description: String?,
    val systemRole: Boolean,
    val scopeType: String,
    val permissions: Set<String>,
)

data class CreateRoleRequest(
    @field:NotBlank val name: String,
    val description: String? = null,
    val scopeType: String = "WORKSPACE",
    val permissions: Set<String> = emptySet(),
)

data class AssignRoleRequest(
    val memberId: UUID,
    val roleId: UUID,
    @field:NotBlank val scopeType: String,
    @field:NotBlank val scopeId: String,
    val approvalSource: String = "MANUAL",
    val expiresAt: Instant? = null,
)

data class CreatePermissionBindingRequest(
    val roleId: UUID,
    @field:NotBlank val action: String,
    @field:NotBlank val resourcePattern: String,
    val effect: String = "ALLOW",
    val condition: String? = null,
    val expiresAt: Instant? = null,
)

data class PermissionBindingResponse(
    val bindingId: UUID,
    val roleId: UUID,
    val action: String,
    val resourcePattern: String,
    val effect: String,
    val condition: String?,
    val expiresAt: Instant?,
)

data class RoleAssignmentResponse(
    val assignmentId: UUID,
    val memberId: UUID,
    val roleId: UUID,
    val scopeType: String,
    val scopeId: String,
    val approvalSource: String,
    val grantedBy: String,
    val expiresAt: Instant?,
    val status: String,
)

data class SessionResponse(
    val sessionId: UUID,
    val deviceName: String,
    val ipAddress: String?,
    val status: String,
    val expiresAt: Instant,
    val createdAt: Instant,
)

fun Member.toResponse(): MemberResponse =
    MemberResponse(memberId, email, displayName, status.name, authProvider.name, mfaEnabled, locale, timezone, lastLoginAt, createdAt)

fun Workspace.toResponse(): WorkspaceResponse =
    WorkspaceResponse(workspaceId, name, description, locale, timezone, signupMode, ownerMemberId, status.name, createdAt)

fun Role.toResponse(): RoleResponse = RoleResponse(roleId, name, description, systemRole, scopeType.name, permissions)

fun RoleAssignment.toResponse(): RoleAssignmentResponse =
    RoleAssignmentResponse(assignmentId, memberId, roleId, scopeType.name, scopeId, approvalSource, grantedBy, expiresAt, status.name)

fun MemberSession.toResponse(): SessionResponse = SessionResponse(sessionId, deviceName, ipAddress, status.name, expiresAt, createdAt)

fun PermissionBinding.toResponse(): PermissionBindingResponse =
    PermissionBindingResponse(bindingId, roleId, action, resourcePattern, effect, condition, expiresAt)
