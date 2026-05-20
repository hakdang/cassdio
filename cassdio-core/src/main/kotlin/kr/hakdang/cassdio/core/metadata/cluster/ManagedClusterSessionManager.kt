package kr.hakdang.cassdio.core.metadata.cluster

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.auth.ProgrammaticPlainTextAuthProvider
import kr.hakdang.cassdio.core.exception.NotFoundException
import org.springframework.stereotype.Service
import java.net.InetSocketAddress
import java.time.Clock
import java.time.Instant
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import javax.net.ssl.SSLContext

@Service
class ManagedClusterSessionManager(
    private val repository: ManagedClusterRepository,
    private val encryptionService: EncryptionService,
    private val clock: Clock = Clock.systemUTC(),
) : AutoCloseable {
    private val sessions = ConcurrentHashMap<UUID, ManagedSession>()
    private val clearedAt = ConcurrentHashMap<UUID, Instant>()

    fun getSession(clusterId: UUID): CqlSession {
        val current = sessions[clusterId]
        if (current != null) {
            sessions[clusterId] = current.copy(lastUsedAt = Instant.now(clock))
            return current.session
        }

        return synchronized(this) {
            val synchronizedCurrent = sessions[clusterId]
            if (synchronizedCurrent != null) {
                sessions[clusterId] = synchronizedCurrent.copy(lastUsedAt = Instant.now(clock))
                synchronizedCurrent.session
            } else {
                open(clusterId).also { sessions[clusterId] = it }.session
            }
        }
    }

    fun snapshot(clusterId: UUID): ClusterSessionSnapshot {
        val current = sessions[clusterId]
        if (current != null) {
            return ClusterSessionSnapshot(
                status = ClusterSessionStatus.CONNECTED,
                openedAt = current.openedAt,
                lastUsedAt = current.lastUsedAt,
                lastClearedAt = clearedAt[clusterId],
                lastError = null,
            )
        }

        return ClusterSessionSnapshot(
            status = ClusterSessionStatus.DISCONNECTED,
            openedAt = null,
            lastUsedAt = null,
            lastClearedAt = clearedAt[clusterId],
            lastError = null,
        )
    }

    fun clear(clusterId: UUID): Boolean {
        sessions.remove(clusterId)?.session?.close()
        clearedAt[clusterId] = Instant.now(clock)
        return true
    }

    fun clearAll(clusterIds: List<UUID>): Int {
        clusterIds.forEach(::clear)
        return clusterIds.size
    }

    override fun close() {
        sessions.values.forEach { it.session.close() }
        sessions.clear()
    }

    private fun open(clusterId: UUID): ManagedSession {
        val cluster = repository.findById(clusterId) ?: throw NotFoundException("Cluster not found.")
        val credential = repository.findCredential(clusterId)
        val openedAt = Instant.now(clock)
        val builder =
            CqlSession
                .builder()
                .withLocalDatacenter(cluster.localDatacenter)

        cluster.contactPoints.forEach { contactPoint ->
            builder.addContactPoint(InetSocketAddress(contactPoint, cluster.port))
        }

        val username = encryptionService.decrypt(credential?.usernameCiphertext)
        if (!username.isNullOrBlank()) {
            builder.withAuthProvider(
                ProgrammaticPlainTextAuthProvider(
                    username,
                    encryptionService.decrypt(credential?.passwordCiphertext).orEmpty(),
                ),
            )
        }

        if (credential?.tlsEnabled == true) {
            builder.withSslContext(SSLContext.getDefault())
        }

        return ManagedSession(session = builder.build(), openedAt = openedAt, lastUsedAt = openedAt)
    }

    private data class ManagedSession(
        val session: CqlSession,
        val openedAt: Instant,
        val lastUsedAt: Instant,
    )
}
