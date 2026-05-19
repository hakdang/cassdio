package kr.hakdang.cassdio.web.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kr.hakdang.cassdio.core.identity.AuthService
import kr.hakdang.cassdio.core.identity.TokenService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
@ConditionalOnBean(AuthService::class, TokenService::class)
class AccessTokenFilter(
    private val tokenService: TokenService,
    private val authService: AuthService,
) : OncePerRequestFilter() {
    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.requestURI
        return !path.startsWith("/api/") ||
            path == "/api/auth/login" ||
            path == "/api/auth/refresh" ||
            path.startsWith("/api/health") ||
            path.startsWith("/api/version") ||
            path.startsWith("/api/metadata")
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            val token =
                request
                    .getHeader("Authorization")
                    ?.takeIf { it.startsWith("Bearer ") }
                    ?.removePrefix("Bearer ")
            if (token.isNullOrBlank()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing access token.")
                return
            }
            val member = authService.validateAccess(tokenService.verifyAccessToken(token))
            CurrentMemberContext.set(member)
            filterChain.doFilter(request, response)
        } catch (exception: Exception) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, exception.message ?: "Unauthorized.")
        } finally {
            CurrentMemberContext.clear()
        }
    }
}
