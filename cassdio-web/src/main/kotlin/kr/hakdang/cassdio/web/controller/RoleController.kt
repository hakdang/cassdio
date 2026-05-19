package kr.hakdang.cassdio.web.controller

import jakarta.validation.Valid
import kr.hakdang.cassdio.core.api.ApiResponse
import kr.hakdang.cassdio.core.identity.AuditService
import kr.hakdang.cassdio.core.identity.PermissionBinding
import kr.hakdang.cassdio.core.identity.PermissionBindingRepository
import kr.hakdang.cassdio.core.identity.Role
import kr.hakdang.cassdio.core.identity.RoleAssignment
import kr.hakdang.cassdio.core.identity.RoleAssignmentStatus
import kr.hakdang.cassdio.core.identity.RoleRepository
import kr.hakdang.cassdio.core.identity.RoleScopeType
import kr.hakdang.cassdio.web.dto.AssignRoleRequest
import kr.hakdang.cassdio.web.dto.CreatePermissionBindingRequest
import kr.hakdang.cassdio.web.dto.CreateRoleRequest
import kr.hakdang.cassdio.web.dto.PermissionBindingResponse
import kr.hakdang.cassdio.web.dto.RoleAssignmentResponse
import kr.hakdang.cassdio.web.dto.RoleResponse
import kr.hakdang.cassdio.web.dto.toResponse
import kr.hakdang.cassdio.web.security.CurrentMemberContext
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.util.UUID

@RestController
@RequestMapping("/api/roles")
class RoleController(
    private val roleRepository: RoleRepository,
    private val permissionBindingRepository: PermissionBindingRepository,
    private val auditService: AuditService,
) {
    @GetMapping
    fun list(): ApiResponse<List<RoleResponse>> = ApiResponse.success(roleRepository.list().map { it.toResponse() })

    @PostMapping
    fun create(
        @Valid @RequestBody request: CreateRoleRequest,
    ): ApiResponse<RoleResponse> {
        val now = Instant.now()
        val role =
            Role(
                roleId = UUID.randomUUID(),
                name = request.name,
                description = request.description,
                systemRole = false,
                scopeType = RoleScopeType.valueOf(request.scopeType),
                permissions = request.permissions,
                createdAt = now,
                updatedAt = now,
            )
        roleRepository.save(role)
        auditService.record("ROLE_CREATED", actor(), "role", role.roleId.toString())
        return ApiResponse.success(role.toResponse())
    }

    @PostMapping("/assignments")
    fun assign(
        @Valid @RequestBody request: AssignRoleRequest,
    ): ApiResponse<RoleAssignmentResponse> {
        val now = Instant.now()
        val assignment =
            RoleAssignment(
                assignmentId = UUID.randomUUID(),
                memberId = request.memberId,
                roleId = request.roleId,
                scopeType = RoleScopeType.valueOf(request.scopeType),
                scopeId = request.scopeId,
                approvalSource = request.approvalSource,
                grantedBy = actor(),
                grantedAt = now,
                effectiveFrom = now,
                expiresAt = request.expiresAt,
                status = RoleAssignmentStatus.ACTIVE,
            )
        roleRepository.saveAssignment(assignment)
        auditService.record("ROLE_ASSIGNED", actor(), "member", request.memberId.toString(), mapOf("roleId" to request.roleId.toString()))
        return ApiResponse.success(assignment.toResponse())
    }

    @GetMapping("/assignments/member/{memberId}")
    fun assignments(
        @PathVariable memberId: UUID,
    ): ApiResponse<List<RoleAssignmentResponse>> =
        ApiResponse.success(roleRepository.assignmentsForMember(memberId).map { it.toResponse() })

    @PostMapping("/permissions")
    fun bindPermission(
        @Valid @RequestBody request: CreatePermissionBindingRequest,
    ): ApiResponse<PermissionBindingResponse> {
        val binding =
            PermissionBinding(
                bindingId = UUID.randomUUID(),
                roleId = request.roleId,
                action = request.action,
                resourcePattern = request.resourcePattern,
                effect = request.effect,
                condition = request.condition,
                expiresAt = request.expiresAt,
                createdAt = Instant.now(),
            )
        permissionBindingRepository.save(binding)
        auditService.record("ROLE_PERMISSION_BOUND", actor(), "role", request.roleId.toString(), mapOf("action" to request.action))
        return ApiResponse.success(binding.toResponse())
    }

    @GetMapping("/{roleId}/permissions")
    fun permissions(
        @PathVariable roleId: UUID,
    ): ApiResponse<List<PermissionBindingResponse>> =
        ApiResponse.success(
            permissionBindingRepository.listByRole(roleId).map {
                it.toResponse()
            },
        )

    private fun actor(): String = CurrentMemberContext.get()?.memberId?.toString() ?: "System"
}
