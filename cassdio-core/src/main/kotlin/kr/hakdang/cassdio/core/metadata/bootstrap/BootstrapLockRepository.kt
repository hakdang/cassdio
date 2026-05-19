package kr.hakdang.cassdio.core.metadata.bootstrap

import kr.hakdang.cassdio.core.metadata.config.MetadataDbConfigProvider
import kr.hakdang.cassdio.core.metadata.cql.CqlExecutor
import org.springframework.stereotype.Repository
import java.time.Clock
import java.time.Duration
import java.time.Instant

@Repository
class BootstrapLockRepository(
    private val configProvider: MetadataDbConfigProvider,
    private val cqlExecutor: CqlExecutor,
    private val clock: Clock = Clock.systemUTC(),
) {
    fun acquire(
        name: String,
        owner: String,
        ttl: Duration,
    ): BootstrapLock {
        val now = Instant.now(clock)
        val expiresAt = now.plus(ttl)
        val keyspace = configProvider.getConfig().keyspace
        val statement =
            """
            INSERT INTO $keyspace.bootstrap_locks (name, owner, status, heartbeat_at, expires_at)
            VALUES (${name.cqlLiteral()}, ${owner.cqlLiteral()}, 'RUNNING', ${now.timestampLiteral()}, ${expiresAt.timestampLiteral()})
            IF NOT EXISTS
            """.trimIndent()

        val applied = cqlExecutor.queryOne(statement)?.boolean("[applied]") ?: false
        if (applied) {
            return BootstrapLock(name, owner, BootstrapLockStatus.RUNNING, now, expiresAt)
        }

        val current = currentLock(name)
        if (current != null && current.status != BootstrapLockStatus.RUNNING) {
            return replaceInactiveLock(current, owner, now, expiresAt)
        }

        val expired = current?.expiresAt?.isBefore(now) ?: false
        if (expired) {
            return replaceInactiveLock(current, owner, now, expiresAt)
        }

        throw MetadataBootstrapLockException("Metadata bootstrap lock is already held for $name.")
    }

    private fun currentLock(name: String): BootstrapLock? {
        val keyspace = configProvider.getConfig().keyspace
        val row =
            cqlExecutor.queryOne(
                "SELECT owner, status, expires_at FROM $keyspace.bootstrap_locks " +
                    "WHERE name = ${name.cqlLiteral()}",
            ) ?: return null

        return BootstrapLock(
            name = name,
            owner = row.string("owner") ?: "",
            status = row.string("status")?.let(BootstrapLockStatus::valueOf) ?: BootstrapLockStatus.RUNNING,
            heartbeatAt = Instant.EPOCH,
            expiresAt = row.string("expires_at")?.let(Instant::parse) ?: Instant.EPOCH,
        )
    }

    private fun replaceInactiveLock(
        current: BootstrapLock,
        owner: String,
        heartbeatAt: Instant,
        expiresAt: Instant,
    ): BootstrapLock {
        val keyspace = configProvider.getConfig().keyspace
        val statement =
            """
            UPDATE $keyspace.bootstrap_locks
            SET owner = ${owner.cqlLiteral()},
                status = ${BootstrapLockStatus.RUNNING.name.cqlLiteral()},
                heartbeat_at = ${heartbeatAt.timestampLiteral()},
                expires_at = ${expiresAt.timestampLiteral()}
            WHERE name = ${current.name.cqlLiteral()}
            IF owner = ${current.owner.cqlLiteral()}
               AND status = ${current.status.name.cqlLiteral()}
            """.trimIndent()

        val applied = cqlExecutor.queryOne(statement)?.boolean("[applied]") ?: false
        if (!applied) {
            throw MetadataBootstrapLockException("Metadata bootstrap lock is already held for ${current.name}.")
        }

        return BootstrapLock(current.name, owner, BootstrapLockStatus.RUNNING, heartbeatAt, expiresAt)
    }

    fun heartbeat(lock: BootstrapLock): BootstrapLock {
        val now = Instant.now(clock)
        val next = lock.copy(heartbeatAt = now)
        val keyspace = configProvider.getConfig().keyspace

        cqlExecutor.execute(
            "UPDATE $keyspace.bootstrap_locks SET heartbeat_at = ${now.timestampLiteral()} " +
                "WHERE name = ${lock.name.cqlLiteral()}",
        )
        return next
    }

    fun release(
        name: String,
        owner: String,
        status: BootstrapLockStatus,
    ) {
        val keyspace = configProvider.getConfig().keyspace
        cqlExecutor.execute(
            "UPDATE $keyspace.bootstrap_locks SET owner = ${owner.cqlLiteral()}, " +
                "status = ${status.name.cqlLiteral()}, heartbeat_at = ${Instant.now(clock).timestampLiteral()} " +
                "WHERE name = ${name.cqlLiteral()}",
        )
    }
}

internal fun Instant.timestampLiteral(): String = toString().cqlLiteral()
