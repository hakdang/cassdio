package kr.hakdang.cassdio.web.controller

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import kr.hakdang.cassdio.core.api.ApiResponse
import kr.hakdang.cassdio.core.exception.UnauthorizedException
import kr.hakdang.cassdio.core.identity.AuthService
import kr.hakdang.cassdio.web.dto.LoginRequest
import kr.hakdang.cassdio.web.dto.LoginResponse
import kr.hakdang.cassdio.web.dto.SessionResponse
import kr.hakdang.cassdio.web.dto.toResponse
import kr.hakdang.cassdio.web.security.CurrentMemberContext
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Duration
import java.util.UUID

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
) {
    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequest,
        servletRequest: HttpServletRequest,
        response: HttpServletResponse,
    ): ApiResponse<LoginResponse> {
        val result = authService.login(request.email, request.password, servletRequest.remoteAddr, servletRequest.getHeader("User-Agent"))
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie(result.refreshToken).toString())
        return ApiResponse.success(LoginResponse(result.accessToken, result.member.toResponse(), result.session.sessionId))
    }

    @PostMapping("/refresh")
    fun refresh(
        servletRequest: HttpServletRequest,
        response: HttpServletResponse,
    ): ApiResponse<LoginResponse> {
        val result = authService.refresh(refreshToken(servletRequest) ?: throw UnauthorizedException("Refresh token cookie is missing."))
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie(result.refreshToken).toString())
        return ApiResponse.success(LoginResponse(result.accessToken, result.member.toResponse(), result.session.sessionId))
    }

    @PostMapping("/logout")
    fun logout(
        servletRequest: HttpServletRequest,
        response: HttpServletResponse,
    ): ApiResponse<Unit> {
        authService.logout(refreshToken(servletRequest))
        response.addHeader(HttpHeaders.SET_COOKIE, expiredRefreshCookie().toString())
        return ApiResponse.success(Unit)
    }

    @PostMapping("/logout-all")
    fun logoutAll(): ApiResponse<Unit> {
        val member = CurrentMemberContext.get() ?: throw UnauthorizedException()
        authService.logoutAll(member.memberId)
        return ApiResponse.success(Unit)
    }

    @GetMapping("/me")
    fun me(): ApiResponse<LoginResponse> {
        val member = CurrentMemberContext.get() ?: throw UnauthorizedException()
        return ApiResponse.success(LoginResponse(accessToken = "", member = member.toResponse(), sessionId = UUID(0, 0)))
    }

    @GetMapping("/sessions")
    fun sessions(): ApiResponse<List<SessionResponse>> {
        val member = CurrentMemberContext.get() ?: throw UnauthorizedException()
        return ApiResponse.success(authService.sessions(member.memberId).map { it.toResponse() })
    }

    @DeleteMapping("/sessions/{sessionId}")
    fun revokeSession(
        @PathVariable sessionId: UUID,
    ): ApiResponse<Unit> {
        val member = CurrentMemberContext.get() ?: throw UnauthorizedException()
        authService.revokeSession(member.memberId, sessionId)
        return ApiResponse.success(Unit)
    }

    private fun refreshToken(request: HttpServletRequest): String? = request.cookies?.firstOrNull { it.name == REFRESH_COOKIE_NAME }?.value

    private fun refreshCookie(value: String): ResponseCookie =
        ResponseCookie
            .from(REFRESH_COOKIE_NAME, value)
            .httpOnly(true)
            .secure(false)
            .sameSite("Lax")
            .path("/api/auth")
            .maxAge(Duration.ofDays(14))
            .build()

    private fun expiredRefreshCookie(): ResponseCookie =
        ResponseCookie
            .from(REFRESH_COOKIE_NAME, "")
            .httpOnly(true)
            .secure(false)
            .sameSite("Lax")
            .path("/api/auth")
            .maxAge(Duration.ZERO)
            .build()

    companion object {
        private const val REFRESH_COOKIE_NAME = "cassdio_refresh_token"
    }
}
