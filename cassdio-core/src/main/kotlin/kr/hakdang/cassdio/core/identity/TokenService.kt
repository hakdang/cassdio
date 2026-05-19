package kr.hakdang.cassdio.core.identity

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.util.Base64
import java.util.UUID
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

data class AccessTokenClaims(
    val memberId: UUID,
    val sessionId: UUID,
    val email: String,
    val workspaceId: UUID?,
    val expiresAt: Instant,
)

@Service
class TokenService(
    @Value("\${cassdio.auth.jwt.secret:dev-only-cassdio-secret-change-me}") private val secret: String,
    @Value("\${cassdio.auth.jwt.issuer:cassdio}") private val issuer: String,
    @Value("\${cassdio.auth.access-token-ttl-minutes:15}") private val accessTokenTtlMinutes: Long,
    private val clock: Clock = Clock.systemUTC(),
    private val objectMapper: ObjectMapper = jacksonObjectMapper(),
) {
    fun createAccessToken(
        member: Member,
        sessionId: UUID,
    ): String {
        val now = Instant.now(clock)
        val header = mapOf("alg" to "HS256", "typ" to "JWT")
        val payload =
            mapOf(
                "iss" to issuer,
                "sub" to member.memberId.toString(),
                "sid" to sessionId.toString(),
                "email" to member.email,
                "workspace_id" to member.workspaceId?.toString(),
                "token_type" to "access",
                "iat" to now.epochSecond,
                "exp" to now.plus(Duration.ofMinutes(accessTokenTtlMinutes)).epochSecond,
            )
        val unsigned = "${base64Json(header)}.${base64Json(payload)}"
        return "$unsigned.${sign(unsigned)}"
    }

    fun verifyAccessToken(token: String): AccessTokenClaims {
        val parts = token.split(".")
        require(parts.size == 3) { "Invalid access token." }
        val unsigned = "${parts[0]}.${parts[1]}"
        require(constantTimeEquals(sign(unsigned), parts[2])) { "Invalid access token signature." }

        val payload = objectMapper.readValue(base64UrlDecoder.decode(parts[1]), Map::class.java)
        require(payload["iss"] == issuer) { "Invalid token issuer." }
        require(payload["token_type"] == "access") { "Invalid token type." }
        val expiresAt = Instant.ofEpochSecond((payload["exp"] as Number).toLong())
        require(expiresAt.isAfter(Instant.now(clock))) { "Access token expired." }

        return AccessTokenClaims(
            memberId = UUID.fromString(payload["sub"].toString()),
            sessionId = UUID.fromString(payload["sid"].toString()),
            email = payload["email"].toString(),
            workspaceId = payload["workspace_id"]?.toString()?.takeIf { it != "null" }?.let(UUID::fromString),
            expiresAt = expiresAt,
        )
    }

    private fun base64Json(value: Any): String = base64UrlEncoder.encodeToString(objectMapper.writeValueAsBytes(value))

    private fun sign(value: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(secret.toByteArray(StandardCharsets.UTF_8), "HmacSHA256"))
        return base64UrlEncoder.encodeToString(mac.doFinal(value.toByteArray(StandardCharsets.UTF_8)))
    }

    private fun constantTimeEquals(
        expected: String,
        actual: String,
    ): Boolean = MessageDigestSupport.equals(expected.toByteArray(StandardCharsets.UTF_8), actual.toByteArray(StandardCharsets.UTF_8))

    companion object {
        private val base64UrlEncoder: Base64.Encoder = Base64.getUrlEncoder().withoutPadding()
        private val base64UrlDecoder: Base64.Decoder = Base64.getUrlDecoder()
    }
}

private object MessageDigestSupport {
    fun equals(
        left: ByteArray,
        right: ByteArray,
    ): Boolean = java.security.MessageDigest.isEqual(left, right)
}
