package kr.hakdang.cassdio.web.controller

import jakarta.validation.Valid
import kr.hakdang.cassdio.core.api.ApiResponse
import kr.hakdang.cassdio.core.exception.NotFoundException
import kr.hakdang.cassdio.core.identity.AuditService
import kr.hakdang.cassdio.core.identity.Workspace
import kr.hakdang.cassdio.core.identity.WorkspaceRepository
import kr.hakdang.cassdio.core.identity.WorkspaceStatus
import kr.hakdang.cassdio.web.dto.CreateWorkspaceRequest
import kr.hakdang.cassdio.web.dto.WorkspaceResponse
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
@RequestMapping("/api/workspaces")
class WorkspaceController(
    private val workspaceRepository: WorkspaceRepository,
    private val auditService: AuditService,
) {
    @GetMapping
    fun list(): ApiResponse<List<WorkspaceResponse>> = ApiResponse.success(workspaceRepository.list().map { it.toResponse() })

    @GetMapping("/{workspaceId}")
    fun detail(
        @PathVariable workspaceId: UUID,
    ): ApiResponse<WorkspaceResponse> =
        ApiResponse.success((workspaceRepository.findById(workspaceId) ?: throw NotFoundException("Workspace not found.")).toResponse())

    @PostMapping
    fun create(
        @Valid @RequestBody request: CreateWorkspaceRequest,
    ): ApiResponse<WorkspaceResponse> {
        val now = Instant.now()
        val workspace =
            Workspace(
                workspaceId = UUID.randomUUID(),
                name = request.name,
                description = request.description,
                locale = request.locale,
                timezone = request.timezone,
                signupMode = request.signupMode,
                ownerMemberId = request.ownerMemberId,
                status = WorkspaceStatus.ACTIVE,
                createdAt = now,
                updatedAt = now,
            )
        workspaceRepository.save(workspace)
        auditService.record(
            "WORKSPACE_CREATED",
            CurrentMemberContext.get()?.memberId?.toString() ?: "System",
            "workspace",
            workspace.workspaceId.toString(),
        )
        return ApiResponse.success(workspace.toResponse())
    }
}
