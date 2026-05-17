package kr.hakdang.cassdio.core.metadata.bootstrap

import java.time.Duration
import java.time.Instant
import java.util.UUID

data class MetadataMigration(
    val version: String,
    val description: String,
    val statements: List<String>,
) {
    init {
        require(version.isNotBlank()) { "Migration version must not be blank." }
        require(description.isNotBlank()) { "Migration description must not be blank." }
        require(statements.isNotEmpty()) { "Migration must contain at least one statement." }
    }
}

data class SeedDefinition(
    val idempotencyKey: String,
    val description: String,
    val statements: List<String>,
) {
    init {
        require(idempotencyKey.isNotBlank()) { "Seed idempotency key must not be blank." }
        require(description.isNotBlank()) { "Seed description must not be blank." }
        require(statements.isNotEmpty()) { "Seed must contain at least one statement." }
    }
}

data class BootstrapLock(
    val name: String,
    val owner: String,
    val status: BootstrapLockStatus,
    val heartbeatAt: Instant,
    val expiresAt: Instant,
)

enum class BootstrapLockStatus {
    RUNNING,
    RELEASED,
    FAILED,
}

data class InstallationState(
    val installationId: UUID,
    val bootstrapCompletedAt: Instant?,
    val schemaVersion: String?,
    val cassdioVersion: String,
)

data class BootstrapResult(
    val installationId: UUID,
    val schemaVersion: String?,
    val executedMigrations: List<String>,
    val executedSeeds: List<String>,
    val duration: Duration,
)

data class MetadataStatus(
    val configured: Boolean,
    val source: String,
    val keyspace: String,
    val connected: Boolean,
    val schemaVersion: String?,
    val bootstrapCompleted: Boolean,
    val installationId: UUID?,
)
