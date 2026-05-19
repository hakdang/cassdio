package kr.hakdang.cassdio.web.controller

import jakarta.validation.Valid
import kr.hakdang.cassdio.core.api.ApiResponse
import kr.hakdang.cassdio.core.exception.NotFoundException
import kr.hakdang.cassdio.core.identity.AuditService
import kr.hakdang.cassdio.core.identity.AuthProvider
import kr.hakdang.cassdio.core.identity.Member
import kr.hakdang.cassdio.core.identity.MemberRepository
import kr.hakdang.cassdio.core.identity.MemberStatus
import kr.hakdang.cassdio.core.identity.MembershipStatus
import kr.hakdang.cassdio.core.identity.WorkspaceMember
import kr.hakdang.cassdio.core.identity.WorkspaceMemberRepository
import kr.hakdang.cassdio.web.dto.CreateMemberRequest
import kr.hakdang.cassdio.web.dto.MemberResponse
import kr.hakdang.cassdio.web.dto.UpdateMemberStatusRequest
import kr.hakdang.cassdio.web.dto.toResponse
import kr.hakdang.cassdio.web.security.CurrentMemberContext
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.util.UUID

@RestController
@RequestMapping("/api/members")
class MemberController(
    private val memberRepository: MemberRepository,
    private val workspaceMemberRepository: WorkspaceMemberRepository,
    private val auditService: AuditService,
    private val passwordEncoder: BCryptPasswordEncoder = BCryptPasswordEncoder(),
) {
    @GetMapping
    fun list(
        @RequestParam(required = false) status: MemberStatus?,
    ): ApiResponse<List<MemberResponse>> = ApiResponse.success(memberRepository.list(status).map { it.toResponse() })

    @GetMapping("/{memberId}")
    fun detail(
        @PathVariable memberId: UUID,
    ): ApiResponse<MemberResponse> =
        ApiResponse.success((memberRepository.findById(memberId) ?: throw NotFoundException("Member not found.")).toResponse())

    @PostMapping
    fun create(
        @Valid @RequestBody request: CreateMemberRequest,
    ): ApiResponse<MemberResponse> {
        val now = Instant.now()
        val member =
            Member(
                memberId = UUID.randomUUID(),
                workspaceId = request.workspaceId,
                email = request.email,
                displayName = request.displayName,
                passwordHash = request.password?.let(passwordEncoder::encode),
                status = MemberStatus.PENDING,
                authProvider = AuthProvider.LOCAL,
                mfaEnabled = false,
                locale = request.locale,
                timezone = request.timezone,
                lastLoginAt = null,
                passwordChangedAt = if (request.password == null) null else now,
                createdAt = now,
                updatedAt = now,
            )
        memberRepository.save(member)
        request.workspaceId?.let {
            workspaceMemberRepository.save(
                WorkspaceMember(
                    workspaceId = it,
                    memberId = member.memberId,
                    membershipStatus = MembershipStatus.INVITED,
                    defaultWorkspace = true,
                    joinedAt = null,
                    invitedBy = CurrentMemberContext.get()?.memberId,
                    lastSelectedAt = null,
                ),
            )
        }
        auditService.record("MEMBER_CREATED", actor(), "member", member.memberId.toString())
        return ApiResponse.success(member.toResponse())
    }

    @PatchMapping("/{memberId}/status")
    fun updateStatus(
        @PathVariable memberId: UUID,
        @Valid @RequestBody request: UpdateMemberStatusRequest,
    ): ApiResponse<MemberResponse> {
        val status = MemberStatus.valueOf(request.status)
        memberRepository.updateStatus(memberId, status, Instant.now())
        auditService.record("MEMBER_STATUS_CHANGED", actor(), "member", memberId.toString(), mapOf("status" to status.name))
        return ApiResponse.success(
            (
                memberRepository.findById(memberId)?.copy(status = status)
                    ?: throw NotFoundException("Member not found.")
            ).toResponse(),
        )
    }

    private fun actor(): String = CurrentMemberContext.get()?.memberId?.toString() ?: "System"
}
