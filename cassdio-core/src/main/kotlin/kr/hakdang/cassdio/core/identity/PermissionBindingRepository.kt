package kr.hakdang.cassdio.core.identity

import kr.hakdang.cassdio.core.metadata.config.MetadataDbConfigProvider
import kr.hakdang.cassdio.core.metadata.cql.CqlExecutor
import kr.hakdang.cassdio.core.metadata.cql.CqlRow
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID

@Repository
class PermissionBindingRepository(
    private val configProvider: MetadataDbConfigProvider,
    private val cqlExecutor: CqlExecutor,
) {
    fun save(binding: PermissionBinding) {
        val keyspace = configProvider.getConfig().keyspace
        cqlExecutor.execute(
            """
            INSERT INTO $keyspace.permission_bindings
            (binding_id, role_id, action, resource_pattern, effect, condition, expires_at, created_at)
            VALUES (
              ${binding.bindingId},
              ${binding.roleId},
              ${binding.action.cqlLiteral()},
              ${binding.resourcePattern.cqlLiteral()},
              ${binding.effect.cqlLiteral()},
              ${binding.condition.cqlNullableLiteral()},
              ${binding.expiresAt?.timestampLiteral() ?: "null"},
              ${binding.createdAt.timestampLiteral()}
            )
            """.trimIndent(),
        )
    }

    fun listByRole(roleId: UUID): List<PermissionBinding> {
        val keyspace = configProvider.getConfig().keyspace
        return cqlExecutor.query("SELECT * FROM $keyspace.permission_bindings WHERE role_id = $roleId ALLOW FILTERING").map {
            it.toPermissionBinding()
        }
    }
}

private fun CqlRow.toPermissionBinding(): PermissionBinding =
    PermissionBinding(
        bindingId = requireNotNull(uuid("binding_id")),
        roleId = requireNotNull(uuid("role_id")),
        action = requireNotNull(string("action")),
        resourcePattern = requireNotNull(string("resource_pattern")),
        effect = string("effect") ?: "ALLOW",
        condition = string("condition"),
        expiresAt = instant("expires_at"),
        createdAt = instant("created_at") ?: Instant.EPOCH,
    )
