package kr.hakdang.cassdio.core.identity

import kr.hakdang.cassdio.core.metadata.config.MetadataDbConfigProvider
import kr.hakdang.cassdio.core.metadata.cql.CqlExecutor
import kr.hakdang.cassdio.core.metadata.cql.CqlRow
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID

@Repository
class WorkspaceRepository(
    private val configProvider: MetadataDbConfigProvider,
    private val cqlExecutor: CqlExecutor,
) {
    fun findById(workspaceId: UUID): Workspace? {
        val keyspace = configProvider.getConfig().keyspace
        return cqlExecutor.queryOne("SELECT * FROM $keyspace.workspaces WHERE workspace_id = $workspaceId")?.toWorkspace()
    }

    fun list(): List<Workspace> {
        val keyspace = configProvider.getConfig().keyspace
        return cqlExecutor.query("SELECT * FROM $keyspace.workspaces").map { it.toWorkspace() }
    }

    fun save(workspace: Workspace) {
        val keyspace = configProvider.getConfig().keyspace
        cqlExecutor.execute(
            """
            INSERT INTO $keyspace.workspaces
            (workspace_id, name, description, locale, timezone, signup_mode, owner_member_id, status, created_at, updated_at)
            VALUES (
              ${workspace.workspaceId},
              ${workspace.name.cqlLiteral()},
              ${workspace.description.cqlNullableLiteral()},
              ${workspace.locale.cqlLiteral()},
              ${workspace.timezone.cqlLiteral()},
              ${workspace.signupMode.cqlLiteral()},
              ${workspace.ownerMemberId ?: "null"},
              ${workspace.status.name.cqlLiteral()},
              ${workspace.createdAt.timestampLiteral()},
              ${workspace.updatedAt.timestampLiteral()}
            )
            """.trimIndent(),
        )
    }
}

internal fun CqlRow.toWorkspace(): Workspace =
    Workspace(
        workspaceId = requireNotNull(uuid("workspace_id")),
        name = requireNotNull(string("name")),
        description = string("description"),
        locale = string("locale") ?: "ko-KR",
        timezone = string("timezone") ?: "Asia/Seoul",
        signupMode = string("signup_mode") ?: "MANUAL_APPROVAL",
        ownerMemberId = uuid("owner_member_id"),
        status = WorkspaceStatus.valueOf(string("status") ?: WorkspaceStatus.ACTIVE.name),
        createdAt = instant("created_at") ?: Instant.EPOCH,
        updatedAt = instant("updated_at") ?: Instant.EPOCH,
    )
