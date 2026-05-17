package kr.hakdang.cassdio.core.metadata.bootstrap

import com.datastax.oss.driver.api.core.CqlSession
import kr.hakdang.cassdio.core.metadata.config.MetadataBootstrapProperties
import kr.hakdang.cassdio.core.metadata.config.MetadataDbConfig
import kr.hakdang.cassdio.core.metadata.config.MetadataDbConfigProvider
import kr.hakdang.cassdio.core.metadata.config.MetadataDbConfigSource
import kr.hakdang.cassdio.core.metadata.config.ReplicationProperties
import kr.hakdang.cassdio.core.metadata.config.ReplicationStrategy
import kr.hakdang.cassdio.core.metadata.cql.CassandraCqlExecutor
import kr.hakdang.cassdio.core.metadata.cql.CqlExecutor
import kr.hakdang.cassdio.core.metadata.cql.CqlRow
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class MetadataBootstrapTests {
    @Test
    fun `keyspace creation uses network topology replication`() {
        val executor = RecordingCqlExecutor()
        val service =
            KeyspaceCreationService(
                configProvider = testConfigProvider(),
                bootstrapProperties =
                    MetadataBootstrapProperties(
                        replication =
                            ReplicationProperties(
                                strategy = ReplicationStrategy.NETWORK_TOPOLOGY,
                                datacenters = mapOf("dc1" to 3, "dc2" to 2),
                            ),
                    ),
                cqlExecutor = executor,
            )

        val created = service.ensureKeyspace()

        assertTrue(created)
        assertEquals(1, executor.executed.size)
        assertTrue(executor.executed.single().contains("'class': 'NetworkTopologyStrategy'"))
        assertTrue(executor.executed.single().contains("'dc1': 3"))
        assertTrue(executor.executed.single().contains("'dc2': 2"))
    }

    @Test
    fun `keyspace creation is skipped when keyspace already exists`() {
        val executor =
            RecordingCqlExecutor(
                queryHandler = { statement ->
                    if (statement.contains("system_schema.keyspaces")) {
                        CqlRow(mapOf("keyspace_name" to "cassdio_meta"))
                    } else {
                        null
                    }
                },
            )
        val service =
            KeyspaceCreationService(
                configProvider = testConfigProvider(),
                bootstrapProperties = MetadataBootstrapProperties(),
                cqlExecutor = executor,
            )

        val created = service.ensureKeyspace()

        assertEquals(false, created)
        assertTrue(executor.executed.isEmpty())
    }

    @Test
    fun `schema migration records only pending migrations`() {
        val executor =
            RecordingCqlExecutor(
                queryHandler = { statement ->
                    if (statement.contains("schema_migrations") && statement.contains("SELECT success")) {
                        null
                    } else {
                        null
                    }
                },
            )
        val service =
            SchemaMigrationService(
                migrationCatalog = MetadataMigrationCatalog(testConfigProvider()),
                migrationRepository = SchemaMigrationRepository(testConfigProvider(), executor),
                cqlExecutor = executor,
            )

        val executed = service.migrate()

        assertEquals(listOf("202602010001"), executed)
        assertTrue(executor.executed.any { it.contains("CREATE TABLE IF NOT EXISTS cassdio_meta.bootstrap_locks") })
        assertTrue(executor.executed.any { it.contains("INSERT INTO cassdio_meta.schema_migrations") })
    }

    @Test
    fun `bootstrap lock throws when another owner holds an active lock`() {
        val executor =
            RecordingCqlExecutor(
                queryHandler = { statement ->
                    when {
                        statement.contains("IF NOT EXISTS") -> CqlRow(mapOf("[applied]" to false))
                        statement.contains("SELECT owner") ->
                            CqlRow(
                                mapOf(
                                    "owner" to "other-instance",
                                    "status" to "RUNNING",
                                    "expires_at" to "2999-01-01T00:00:00Z",
                                ),
                            )
                        else -> null
                    }
                },
            )
        val repository = BootstrapLockRepository(testConfigProvider(), executor)

        assertFailsWith<MetadataBootstrapLockException> {
            repository.acquire("metadata-bootstrap", "cassdio-web", java.time.Duration.ofMinutes(5))
        }
    }

    @Test
    fun `cassandra executor reconnects when metadata config changes`() {
        var config =
            MetadataDbConfig(
                contactPoints = listOf("127.0.0.1"),
                port = 9042,
                localDatacenter = "datacenter1",
                keyspace = "cassdio_meta",
                source = MetadataDbConfigSource.TEST_FIXTURE,
            )
        val firstSession = mock(CqlSession::class.java)
        val secondSession = mock(CqlSession::class.java)
        val sessions = ArrayDeque(listOf(firstSession, secondSession))
        val executor =
            CassandraCqlExecutor(
                configProvider = MetadataDbConfigProvider { config },
                sessionFactory = { sessions.removeFirst() },
            )

        executor.execute("SELECT 1")
        executor.execute("SELECT 2")
        config = config.copy(contactPoints = listOf("127.0.0.2"))
        executor.execute("SELECT 3")

        verify(firstSession).execute("SELECT 1")
        verify(firstSession).execute("SELECT 2")
        verify(firstSession).close()
        verify(secondSession).execute("SELECT 3")
        verifyNoMoreInteractions(secondSession)
    }

    private fun testConfigProvider(): MetadataDbConfigProvider =
        MetadataDbConfigProvider {
            MetadataDbConfig(
                contactPoints = listOf("127.0.0.1"),
                port = 9042,
                localDatacenter = "datacenter1",
                keyspace = "cassdio_meta",
            )
        }
}

private class RecordingCqlExecutor(
    private val queryHandler: (String) -> CqlRow? = { null },
) : CqlExecutor {
    val executed = mutableListOf<String>()

    override fun execute(statement: String) {
        executed += statement
    }

    override fun queryOne(statement: String): CqlRow? = queryHandler(statement)
}
