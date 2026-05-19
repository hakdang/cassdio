package kr.hakdang.cassdio.core.identity

import kr.hakdang.cassdio.core.metadata.config.MetadataDbConfigProvider
import kr.hakdang.cassdio.core.metadata.cql.CqlExecutor
import kr.hakdang.cassdio.core.metadata.cql.CqlRow
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID

enum class MemberSessionStatus {
    ACTIVE,
    ROTATED,
    REVOKED,
    EXPIRED,
    COMPROMISED,
}

data class MemberSession(
    val sessionId: UUID,
    val memberId: UUID,
    val refreshTokenHash: String,
    val tokenFamilyId: UUID,
    val previousTokenHash: String?,
    val deviceName: String,
    val ipAddress: String?,
    val userAgentHash: String?,
    val status: MemberSessionStatus,
    val expiresAt: Instant,
    val rotatedAt: Instant?,
    val revokedAt: Instant?,
    val createdAt: Instant,
)

@Repository
class MemberSessionRepository(
    private val configProvider: MetadataDbConfigProvider,
    private val cqlExecutor: CqlExecutor,
) {
    fun save(session: MemberSession) {
        val keyspace = configProvider.getConfig().keyspace
        cqlExecutor.execute(
            """
            INSERT INTO $keyspace.member_sessions
            (session_id, member_id, refresh_token_hash, token_family_id, previous_token_hash, device_name, ip_address, user_agent_hash, status, expires_at, rotated_at, revoked_at, created_at)
            VALUES (
              ${session.sessionId},
              ${session.memberId},
              ${session.refreshTokenHash.cqlLiteral()},
              ${session.tokenFamilyId},
              ${session.previousTokenHash.cqlNullableLiteral()},
              ${session.deviceName.cqlLiteral()},
              ${session.ipAddress.cqlNullableLiteral()},
              ${session.userAgentHash.cqlNullableLiteral()},
              ${session.status.name.cqlLiteral()},
              ${session.expiresAt.timestampLiteral()},
              ${session.rotatedAt?.timestampLiteral() ?: "null"},
              ${session.revokedAt?.timestampLiteral() ?: "null"},
              ${session.createdAt.timestampLiteral()}
            )
            """.trimIndent(),
        )
    }

    fun findByRefreshTokenHash(hash: String): MemberSession? {
        val keyspace = configProvider.getConfig().keyspace
        return cqlExecutor
            .query(
                "SELECT * FROM $keyspace.member_sessions WHERE refresh_token_hash = ${hash.cqlLiteral()} ALLOW FILTERING",
            ).firstOrNull()
            ?.toMemberSession()
    }

    fun findById(sessionId: UUID): MemberSession? {
        val keyspace = configProvider.getConfig().keyspace
        return cqlExecutor.queryOne("SELECT * FROM $keyspace.member_sessions WHERE session_id = $sessionId")?.toMemberSession()
    }

    fun listByMember(memberId: UUID): List<MemberSession> {
        val keyspace = configProvider.getConfig().keyspace
        return cqlExecutor
            .query(
                "SELECT * FROM $keyspace.member_sessions WHERE member_id = $memberId ALLOW FILTERING",
            ).map { it.toMemberSession() }
    }

    fun updateStatus(
        sessionId: UUID,
        status: MemberSessionStatus,
        changedAt: Instant,
    ) {
        val keyspace = configProvider.getConfig().keyspace
        val column = if (status == MemberSessionStatus.ROTATED) "rotated_at" else "revoked_at"
        cqlExecutor.execute(
            "UPDATE $keyspace.member_sessions SET status = ${status.name.cqlLiteral()}, $column = ${changedAt.timestampLiteral()} WHERE session_id = $sessionId",
        )
    }

    fun revokeFamily(
        tokenFamilyId: UUID,
        changedAt: Instant,
    ) {
        listByFamily(tokenFamilyId).forEach { updateStatus(it.sessionId, MemberSessionStatus.COMPROMISED, changedAt) }
    }

    private fun listByFamily(tokenFamilyId: UUID): List<MemberSession> {
        val keyspace = configProvider.getConfig().keyspace
        return cqlExecutor.query("SELECT * FROM $keyspace.member_sessions WHERE token_family_id = $tokenFamilyId ALLOW FILTERING").map {
            it.toMemberSession()
        }
    }
}

private fun CqlRow.toMemberSession(): MemberSession =
    MemberSession(
        sessionId = requireNotNull(uuid("session_id")),
        memberId = requireNotNull(uuid("member_id")),
        refreshTokenHash = requireNotNull(string("refresh_token_hash")),
        tokenFamilyId = requireNotNull(uuid("token_family_id")),
        previousTokenHash = string("previous_token_hash"),
        deviceName = string("device_name") ?: "Unknown device",
        ipAddress = string("ip_address"),
        userAgentHash = string("user_agent_hash"),
        status = MemberSessionStatus.valueOf(string("status") ?: MemberSessionStatus.ACTIVE.name),
        expiresAt = instant("expires_at") ?: Instant.EPOCH,
        rotatedAt = instant("rotated_at"),
        revokedAt = instant("revoked_at"),
        createdAt = instant("created_at") ?: Instant.EPOCH,
    )
