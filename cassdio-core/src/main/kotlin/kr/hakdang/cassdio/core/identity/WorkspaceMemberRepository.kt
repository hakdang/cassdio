package kr.hakdang.cassdio.core.identity

import kr.hakdang.cassdio.core.metadata.config.MetadataDbConfigProvider
import kr.hakdang.cassdio.core.metadata.cql.CqlExecutor
import kr.hakdang.cassdio.core.metadata.cql.CqlRow
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class WorkspaceMemberRepository(
    private val configProvider: MetadataDbConfigProvider,
    private val cqlExecutor: CqlExecutor,
) {
    fun save(workspaceMember: WorkspaceMember) {
        val keyspace = configProvider.getConfig().keyspace
        cqlExecutor.execute(
            """
            INSERT INTO $keyspace.workspace_members
            (workspace_id, member_id, membership_status, default_workspace, joined_at, invited_by, last_selected_at)
            VALUES (
              ${workspaceMember.workspaceId},
              ${workspaceMember.memberId},
              ${workspaceMember.membershipStatus.name.cqlLiteral()},
              ${workspaceMember.defaultWorkspace},
              ${workspaceMember.joinedAt?.timestampLiteral() ?: "null"},
              ${workspaceMember.invitedBy ?: "null"},
              ${workspaceMember.lastSelectedAt?.timestampLiteral() ?: "null"}
            )
            """.trimIndent(),
        )
    }

    fun listByWorkspace(workspaceId: UUID): List<WorkspaceMember> {
        val keyspace = configProvider.getConfig().keyspace
        return cqlExecutor
            .query(
                "SELECT * FROM $keyspace.workspace_members WHERE workspace_id = $workspaceId",
            ).map { it.toWorkspaceMember() }
    }
}

private fun CqlRow.toWorkspaceMember(): WorkspaceMember =
    WorkspaceMember(
        workspaceId = requireNotNull(uuid("workspace_id")),
        memberId = requireNotNull(uuid("member_id")),
        membershipStatus = MembershipStatus.valueOf(string("membership_status") ?: MembershipStatus.INVITED.name),
        defaultWorkspace = boolean("default_workspace") == true,
        joinedAt = instant("joined_at"),
        invitedBy = uuid("invited_by"),
        lastSelectedAt = instant("last_selected_at"),
    )
