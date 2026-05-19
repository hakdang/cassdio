package kr.hakdang.cassdio.core.identity

import kr.hakdang.cassdio.core.exception.UnauthorizedException
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.security.SecureRandom
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.util.Base64
import java.util.UUID

data class LoginResult(
    val accessToken: String,
    val refreshToken: String,
    val member: Member,
    val session: MemberSession,
)

@Service
class AuthService(
    private val memberRepository: MemberRepository,
    private val sessionRepository: MemberSessionRepository,
    private val tokenService: TokenService,
    private val auditService: AuditService,
    @Value("\${cassdio.auth.refresh-token-ttl-days:14}") private val refreshTokenTtlDays: Long,
    private val passwordEncoder: BCryptPasswordEncoder = BCryptPasswordEncoder(),
    private val secureRandom: SecureRandom = SecureRandom(),
    private val clock: Clock = Clock.systemUTC(),
) {
    fun login(
        email: String,
        password: String,
        ipAddress: String?,
        userAgent: String?,
    ): LoginResult {
        val member = memberRepository.findByEmail(email)
        if (member == null || member.passwordHash == null || !passwordEncoder.matches(password, member.passwordHash)) {
            auditService.record("LOGIN_FAILED", email, "member", email, mapOf("reason" to "BAD_CREDENTIALS", "ip" to ipAddress.orEmpty()))
            throw UnauthorizedException("Invalid email or password.")
        }
        if (member.status != MemberStatus.ACTIVE) {
            auditService.record("LOGIN_FAILED", email, "member", member.memberId.toString(), mapOf("reason" to member.status.name))
            throw UnauthorizedException("Member is not active.")
        }

        val now = Instant.now(clock)
        val refreshToken = randomToken()
        val session =
            MemberSession(
                sessionId = UUID.randomUUID(),
                memberId = member.memberId,
                refreshTokenHash = hash(refreshToken),
                tokenFamilyId = UUID.randomUUID(),
                previousTokenHash = null,
                deviceName = userAgent?.take(80) ?: "Unknown device",
                ipAddress = ipAddress,
                userAgentHash = userAgent?.let(::hash),
                status = MemberSessionStatus.ACTIVE,
                expiresAt = now.plus(Duration.ofDays(refreshTokenTtlDays)),
                rotatedAt = null,
                revokedAt = null,
                createdAt = now,
            )
        sessionRepository.save(session)
        memberRepository.markLogin(member.memberId, now)
        auditService.record(
            "LOGIN_SUCCESS",
            member.memberId.toString(),
            "session",
            session.sessionId.toString(),
            mapOf(
                "ip" to ipAddress.orEmpty(),
            ),
        )

        return LoginResult(tokenService.createAccessToken(member, session.sessionId), refreshToken, member.copy(lastLoginAt = now), session)
    }

    fun refresh(refreshToken: String): LoginResult {
        val now = Instant.now(clock)
        val hash = hash(refreshToken)
        val existing = sessionRepository.findByRefreshTokenHash(hash) ?: throw UnauthorizedException("Refresh session not found.")
        if (existing.status != MemberSessionStatus.ACTIVE) {
            sessionRepository.revokeFamily(existing.tokenFamilyId, now)
            auditService.record("SECURITY_TOKEN_REUSE_DETECTED", existing.memberId.toString(), "session", existing.sessionId.toString())
            throw UnauthorizedException("Refresh token is no longer active.")
        }
        if (!existing.expiresAt.isAfter(now)) {
            sessionRepository.updateStatus(existing.sessionId, MemberSessionStatus.EXPIRED, now)
            throw UnauthorizedException("Refresh token expired.")
        }
        val member = memberRepository.findById(existing.memberId) ?: throw UnauthorizedException("Member not found.")
        if (member.status != MemberStatus.ACTIVE) throw UnauthorizedException("Member is not active.")

        sessionRepository.updateStatus(existing.sessionId, MemberSessionStatus.ROTATED, now)
        val nextRefreshToken = randomToken()
        val nextSession =
            existing.copy(
                sessionId = UUID.randomUUID(),
                refreshTokenHash = hash(nextRefreshToken),
                previousTokenHash = hash,
                status = MemberSessionStatus.ACTIVE,
                rotatedAt = null,
                revokedAt = null,
                createdAt = now,
                expiresAt = now.plus(Duration.ofDays(refreshTokenTtlDays)),
            )
        sessionRepository.save(nextSession)
        auditService.record("TOKEN_REFRESH", member.memberId.toString(), "session", nextSession.sessionId.toString())
        return LoginResult(tokenService.createAccessToken(member, nextSession.sessionId), nextRefreshToken, member, nextSession)
    }

    fun logout(refreshToken: String?) {
        refreshToken ?: return
        sessionRepository.findByRefreshTokenHash(hash(refreshToken))?.let {
            sessionRepository.updateStatus(it.sessionId, MemberSessionStatus.REVOKED, Instant.now(clock))
            auditService.record("LOGOUT", it.memberId.toString(), "session", it.sessionId.toString())
        }
    }

    fun logoutAll(memberId: UUID) {
        val now = Instant.now(clock)
        sessionRepository.listByMember(memberId).filter { it.status == MemberSessionStatus.ACTIVE }.forEach {
            sessionRepository.updateStatus(it.sessionId, MemberSessionStatus.REVOKED, now)
        }
        auditService.record("LOGOUT_ALL", memberId.toString(), "member", memberId.toString())
    }

    fun sessions(memberId: UUID): List<MemberSession> = sessionRepository.listByMember(memberId)

    fun revokeSession(
        memberId: UUID,
        sessionId: UUID,
    ) {
        sessionRepository.updateStatus(sessionId, MemberSessionStatus.REVOKED, Instant.now(clock))
        auditService.record("SESSION_REVOKED", memberId.toString(), "session", sessionId.toString())
    }

    fun validateAccess(claims: AccessTokenClaims): Member {
        val session = sessionRepository.findById(claims.sessionId) ?: throw UnauthorizedException("Session not found.")
        if (session.status != MemberSessionStatus.ACTIVE || !session.expiresAt.isAfter(Instant.now(clock))) {
            throw UnauthorizedException("Session is not active.")
        }
        val member = memberRepository.findById(claims.memberId) ?: throw UnauthorizedException("Member not found.")
        if (member.status != MemberStatus.ACTIVE) throw UnauthorizedException("Member is not active.")
        return member
    }

    private fun randomToken(): String {
        val bytes = ByteArray(48)
        secureRandom.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

    private fun hash(value: String): String =
        Base64.getUrlEncoder().withoutPadding().encodeToString(
            MessageDigest.getInstance("SHA-256").digest(value.toByteArray(Charsets.UTF_8)),
        )
}
