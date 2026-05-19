package kr.hakdang.cassdio.core.identity

import kr.hakdang.cassdio.core.metadata.config.MetadataDbConfigProvider
import kr.hakdang.cassdio.core.metadata.cql.CqlExecutor
import kr.hakdang.cassdio.core.metadata.cql.CqlRow
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID

@Repository
class MemberRepository(
    private val configProvider: MetadataDbConfigProvider,
    private val cqlExecutor: CqlExecutor,
) {
    fun findById(memberId: UUID): Member? {
        val keyspace = configProvider.getConfig().keyspace
        return cqlExecutor.queryOne("SELECT * FROM $keyspace.members WHERE member_id = $memberId")?.toMember()
    }

    fun findByEmail(email: String): Member? {
        val keyspace = configProvider.getConfig().keyspace
        return cqlExecutor
            .query(
                "SELECT * FROM $keyspace.members WHERE email = ${email.cqlLiteral()} ALLOW FILTERING",
            ).firstOrNull()
            ?.toMember()
    }

    fun list(status: MemberStatus? = null): List<Member> {
        val keyspace = configProvider.getConfig().keyspace
        val filter = status?.let { " WHERE status = ${it.name.cqlLiteral()} ALLOW FILTERING" }.orEmpty()
        return cqlExecutor.query("SELECT * FROM $keyspace.members$filter").map { it.toMember() }
    }

    fun save(member: Member) {
        val keyspace = configProvider.getConfig().keyspace
        cqlExecutor.execute(
            """
            INSERT INTO $keyspace.members
            (member_id, workspace_id, display_name, email, email_verified, password_hash, password_algorithm, mfa_status, status, auth_provider, locale, timezone, last_login_at, password_changed_at, created_at, updated_at)
            VALUES (
              ${member.memberId},
              ${member.workspaceId ?: "null"},
              ${member.displayName.cqlLiteral()},
              ${member.email.cqlLiteral()},
              true,
              ${member.passwordHash.cqlNullableLiteral()},
              'BCRYPT',
              ${if (member.mfaEnabled) "'ENABLED'" else "'DISABLED'"},
              ${member.status.name.cqlLiteral()},
              ${member.authProvider.name.cqlLiteral()},
              ${member.locale.cqlLiteral()},
              ${member.timezone.cqlLiteral()},
              ${member.lastLoginAt?.timestampLiteral() ?: "null"},
              ${member.passwordChangedAt?.timestampLiteral() ?: "null"},
              ${member.createdAt.timestampLiteral()},
              ${member.updatedAt.timestampLiteral()}
            )
            """.trimIndent(),
        )
    }

    fun updateStatus(
        memberId: UUID,
        status: MemberStatus,
        updatedAt: Instant,
    ) {
        val keyspace = configProvider.getConfig().keyspace
        cqlExecutor.execute(
            "UPDATE $keyspace.members SET status = ${status.name.cqlLiteral()}, updated_at = ${updatedAt.timestampLiteral()} WHERE member_id = $memberId",
        )
    }

    fun markLogin(
        memberId: UUID,
        loggedInAt: Instant,
    ) {
        val keyspace = configProvider.getConfig().keyspace
        cqlExecutor.execute(
            "UPDATE $keyspace.members SET last_login_at = ${loggedInAt.timestampLiteral()}, updated_at = ${loggedInAt.timestampLiteral()} WHERE member_id = $memberId",
        )
    }
}

internal fun CqlRow.toMember(): Member =
    Member(
        memberId = requireNotNull(uuid("member_id")),
        workspaceId = uuid("workspace_id"),
        email = requireNotNull(string("email")),
        displayName = string("display_name") ?: string("email").orEmpty(),
        passwordHash = string("password_hash"),
        status = MemberStatus.valueOf(string("status") ?: MemberStatus.PENDING.name),
        authProvider = AuthProvider.valueOf(string("auth_provider") ?: AuthProvider.LOCAL.name),
        mfaEnabled = string("mfa_status") == "ENABLED" || boolean("mfa_enabled") == true,
        locale = string("locale") ?: "ko-KR",
        timezone = string("timezone") ?: "Asia/Seoul",
        lastLoginAt = instant("last_login_at"),
        passwordChangedAt = instant("password_changed_at"),
        createdAt = instant("created_at") ?: Instant.EPOCH,
        updatedAt = instant("updated_at") ?: Instant.EPOCH,
    )
