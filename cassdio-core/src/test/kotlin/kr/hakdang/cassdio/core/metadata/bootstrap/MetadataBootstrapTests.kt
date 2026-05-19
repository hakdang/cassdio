package kr.hakdang.cassdio.core.metadata.bootstrap

import com.datastax.oss.driver.api.core.CqlSession
import kr.hakdang.cassdio.core.metadata.cluster.AesGcmEncryptionService
import kr.hakdang.cassdio.core.metadata.cluster.ClusterConnectionService
import kr.hakdang.cassdio.core.metadata.cluster.ClusterHealthChecker
import kr.hakdang.cassdio.core.metadata.cluster.InitialManagedClusterRegistrationService
import kr.hakdang.cassdio.core.metadata.cluster.ManagedClusterProbeClient
import kr.hakdang.cassdio.core.metadata.cluster.ManagedClusterProbeClientFactory
import kr.hakdang.cassdio.core.metadata.cluster.ManagedClusterRegistrationRequest
import kr.hakdang.cassdio.core.metadata.cluster.ManagedClusterRepository
import kr.hakdang.cassdio.core.metadata.config.InitialManagedClusterProperties
import kr.hakdang.cassdio.core.metadata.config.ManagedClusterEnvironment
import kr.hakdang.cassdio.core.metadata.config.MetadataBootstrapProperties
import kr.hakdang.cassdio.core.metadata.config.MetadataDbConfig
import kr.hakdang.cassdio.core.metadata.config.MetadataDbConfigProvider
import kr.hakdang.cassdio.core.metadata.config.MetadataDbConfigSource
import kr.hakdang.cassdio.core.metadata.config.MetadataEncryptionProperties
import kr.hakdang.cassdio.core.metadata.config.MetadataSeedProperties
import kr.hakdang.cassdio.core.metadata.config.ReplicationProperties
import kr.hakdang.cassdio.core.metadata.config.ReplicationStrategy
import kr.hakdang.cassdio.core.metadata.config.SuperAdminProperties
import kr.hakdang.cassdio.core.metadata.cql.CassandraCqlExecutor
import kr.hakdang.cassdio.core.metadata.cql.CqlExecutor
import kr.hakdang.cassdio.core.metadata.cql.CqlRow
import kr.hakdang.cassdio.core.metadata.cql.MetadataCqlUnavailableException
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MetadataBootstrapTests {
    @Test
    fun `metadata db config masks password and validates connection fields`() {
        val config =
            MetadataDbConfig(
                contactPoints = listOf("10.0.0.1", "10.0.0.2"),
                port = 9042,
                localDatacenter = "dc1",
                keyspace = "cassdio_meta",
                username = "cassdio",
                password = "plain-secret",
                tlsEnabled = true,
                source = MetadataDbConfigSource.TEST_FIXTURE,
            )

        val masked = config.masked()

        config.validate()
        assertEquals("plain-secret", config.password)
        assertEquals("******", masked.password)
        assertEquals("cassdio", masked.username)
        assertEquals(MetadataDbConfigSource.TEST_FIXTURE, masked.source)
        assertFailsWith<IllegalArgumentException> {
            config.copy(contactPoints = listOf("127.0.0.1", " ")).validate()
        }
        assertFailsWith<IllegalArgumentException> {
            config.copy(port = 0).validate()
        }
        assertFailsWith<IllegalArgumentException> {
            config.copy(keyspace = "1_invalid").validate()
        }
    }

    @Test
    fun `metadata status reports disconnected without reading installation state`() {
        val executor =
            RecordingCqlExecutor(
                queryHandler = {
                    throw MetadataCqlUnavailableException("metadata store unavailable")
                },
            )
        val service =
            MetadataStatusService(
                configProvider = testConfigProvider(),
                cqlExecutor = executor,
                installationStateRepository = InstallationStateRepository(testConfigProvider(), executor),
            )

        val status = service.status()

        assertTrue(status.configured)
        assertEquals("APPLICATION_CONFIG", status.source)
        assertEquals("cassdio_meta", status.keyspace)
        assertFalse(status.connected)
        assertFalse(status.bootstrapCompleted)
        assertEquals(null, status.schemaVersion)
        assertEquals(null, status.installationId)
        assertEquals(listOf("SELECT release_version FROM system.local"), executor.queried)
    }

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

        assertEquals(listOf("202602010001", "202605190001", "202605190002"), executed)
        assertTrue(executor.executed.any { it.contains("CREATE TABLE IF NOT EXISTS cassdio_meta.bootstrap_locks") })
        assertTrue(executor.executed.any { it.contains("CREATE TABLE IF NOT EXISTS cassdio_meta.workspaces") })
        assertTrue(executor.executed.any { it.contains("CREATE TABLE IF NOT EXISTS cassdio_meta.managed_clusters") })
        assertTrue(executor.executed.any { it.contains("INSERT INTO cassdio_meta.schema_migrations") })
    }

    @Test
    fun `metadata seed catalog creates phase 2 milestone 3 default data`() {
        val catalog =
            MetadataSeedCatalog(
                configProvider = testConfigProvider(),
                bootstrapProperties =
                    MetadataBootstrapProperties(
                        seed =
                            MetadataSeedProperties(
                                superAdmin =
                                    SuperAdminProperties(
                                        email = "root@example.com",
                                        initialPassword = "plain-password",
                                    ),
                            ),
                    ),
                passwordHashService = StaticPasswordHashService("bcrypt-hash"),
            )

        val seeds = catalog.seeds()
        val statements = seeds.flatMap { it.statements }

        assertEquals(
            listOf(
                "phase-2-m3-default-workspace",
                "phase-2-m3-super-admin-member",
                "phase-2-m3-system-roles",
                "phase-2-m3-super-admin-role-assignment",
                "phase-2-m3-default-query-policies",
                "phase-2-m3-default-workflow-policies",
                "phase-2-m3-bootstrap-audit-log",
            ),
            seeds.map { it.idempotencyKey },
        )
        assertTrue(statements.any { it.contains("INSERT INTO cassdio_meta.workspaces") })
        assertTrue(statements.any { it.contains("'MANUAL_APPROVAL'") })
        assertTrue(statements.any { it.contains("INSERT INTO cassdio_meta.members") })
        assertTrue(statements.any { it.contains("'root@example.com'") })
        assertTrue(statements.any { it.contains("'bcrypt-hash'") })
        assertTrue(statements.none { it.contains("plain-password") })
        assertTrue(statements.any { it.contains("'Super Admin'") && it.contains("'*'") })
        assertTrue(statements.any { it.contains("INSERT INTO cassdio_meta.role_assignments") })
        assertTrue(statements.any { it.contains("'APPLICATION'") && it.contains("'BOOTSTRAP'") })
        assertTrue(statements.any { it.contains("INSERT INTO cassdio_meta.query_policies") && it.contains("'SELECT'") })
        assertTrue(statements.any { it.contains("'limit.required': 'true'") })
        assertTrue(statements.any { it.contains("INSERT INTO cassdio_meta.workflow_policies") && it.contains("'TABLE_CREATION'") })
        assertTrue(statements.any { it.contains("INSERT INTO cassdio_meta.audit_logs") && it.contains("'BOOTSTRAP_COMPLETED'") })
    }

    @Test
    fun `metadata seed catalog validates super admin email`() {
        val catalog =
            MetadataSeedCatalog(
                configProvider = testConfigProvider(),
                bootstrapProperties =
                    MetadataBootstrapProperties(
                        seed =
                            MetadataSeedProperties(
                                superAdmin = SuperAdminProperties(email = "invalid-email"),
                            ),
                    ),
                passwordHashService = StaticPasswordHashService("bcrypt-hash"),
            )

        assertFailsWith<IllegalArgumentException> {
            catalog.seeds()
        }
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
    fun `bootstrap lock can be acquired again after release`() {
        val executor =
            RecordingCqlExecutor(
                queryHandler = { statement ->
                    when {
                        statement.contains("IF NOT EXISTS") -> CqlRow(mapOf("[applied]" to false))
                        statement.contains("SELECT owner") ->
                            CqlRow(
                                mapOf(
                                    "owner" to "previous-instance",
                                    "status" to "RELEASED",
                                    "expires_at" to "2026-02-01T00:00:00Z",
                                ),
                            )
                        statement.contains("IF owner") -> CqlRow(mapOf("[applied]" to true))
                        else -> null
                    }
                },
            )
        val repository = BootstrapLockRepository(testConfigProvider(), executor)

        val lock = repository.acquire("metadata-bootstrap", "cassdio-web", java.time.Duration.ofMinutes(5))

        assertEquals("cassdio-web", lock.owner)
        assertEquals(BootstrapLockStatus.RUNNING, lock.status)
        assertTrue(executor.queried.any { it.contains("IF owner = 'previous-instance'") })
    }

    @Test
    fun `bootstrap creates keyspace and bootstrap tables before acquiring lock`() {
        val executor =
            RecordingCqlExecutor(
                queryHandler = { statement ->
                    when {
                        statement.contains("INSERT INTO cassdio_meta.bootstrap_locks") ->
                            CqlRow(mapOf("[applied]" to true))
                        else -> null
                    }
                },
            )
        val configProvider = testConfigProvider()
        val migrationCatalog = MetadataMigrationCatalog(configProvider)
        val service =
            MetadataBootstrapService(
                properties = MetadataBootstrapProperties(),
                keyspaceCreationService =
                    KeyspaceCreationService(
                        configProvider = configProvider,
                        bootstrapProperties = MetadataBootstrapProperties(),
                        cqlExecutor = executor,
                    ),
                schemaDefinitions = MetadataSchemaDefinitions(configProvider, executor),
                lockRepository = BootstrapLockRepository(configProvider, executor),
                migrationService =
                    SchemaMigrationService(
                        migrationCatalog = migrationCatalog,
                        migrationRepository = SchemaMigrationRepository(configProvider, executor),
                        cqlExecutor = executor,
                    ),
                seedService = SeedService(MetadataSeedCatalog(), SeedHistoryRepository(configProvider, executor), executor),
                installationStateRepository = InstallationStateRepository(configProvider, executor),
                migrationCatalog = migrationCatalog,
                configProvider = configProvider,
            )

        service.bootstrap()

        val createKeyspaceIndex = executor.events.indexOfFirst { it.contains("CREATE KEYSPACE IF NOT EXISTS cassdio_meta") }
        val createLockTableIndex = executor.events.indexOfFirst { it.contains("CREATE TABLE IF NOT EXISTS cassdio_meta.bootstrap_locks") }
        val acquireLockIndex = executor.events.indexOfFirst { it.contains("INSERT INTO cassdio_meta.bootstrap_locks") }

        assertTrue(createKeyspaceIndex in 0 until createLockTableIndex)
        assertTrue(createLockTableIndex in 0 until acquireLockIndex)
        assertTrue(executor.executed.any { it.contains("initial_settings") })
        assertTrue(executor.executed.any { it.contains("'metadata.keyspace': 'cassdio_meta'") })
    }

    @Test
    fun `seed failure is recorded as unsuccessful so retry can execute it again`() {
        val seed =
            SeedDefinition(
                idempotencyKey = "phase-2-m2-seed",
                description = "Seed used to verify retry semantics",
                statements = listOf("INSERT INTO cassdio_meta.seed_target (id) VALUES ('one')"),
            )
        val executor =
            RecordingCqlExecutor(
                queryHandler = { statement ->
                    when {
                        statement.contains("SELECT success FROM cassdio_meta.seed_history") ->
                            CqlRow(mapOf("success" to false))
                        else -> null
                    }
                },
                executeHandler = { statement ->
                    if (statement.contains("seed_target")) {
                        throw IllegalStateException("seed write failed")
                    }
                },
            )
        val service =
            SeedService(
                seedCatalog = StaticSeedCatalog(listOf(seed)),
                seedHistoryRepository = SeedHistoryRepository(testConfigProvider(), executor),
                cqlExecutor = executor,
            )

        assertFailsWith<MetadataBootstrapException> {
            service.seed()
        }

        assertTrue(executor.executed.any { it.contains("INSERT INTO cassdio_meta.seed_history") })
        assertTrue(executor.executed.any { it.contains("false") })
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

    @Test
    fun `encryption service encrypts and decrypts managed cluster secrets`() {
        val service =
            AesGcmEncryptionService(
                MetadataBootstrapProperties(
                    encryption = MetadataEncryptionProperties(masterKey = "test-master-key"),
                ),
            )

        val encrypted = service.encrypt("secret-password")

        assertTrue(encrypted != null)
        assertTrue(!encrypted.contains("secret-password"))
        assertEquals("secret-password", service.decrypt(encrypted))
    }

    @Test
    fun `initial managed cluster registration tests connection stores metadata and grants dba`() {
        val executor =
            RecordingCqlExecutor(
                queryHandler = { statement ->
                    when {
                        statement.contains("managed_clusters_by_name") -> null
                        else -> null
                    }
                },
            )
        val properties =
            MetadataBootstrapProperties(
                encryption = MetadataEncryptionProperties(masterKey = "test-master-key"),
                initialCluster =
                    InitialManagedClusterProperties(
                        enabled = true,
                        name = "Local Dev",
                        environment = ManagedClusterEnvironment.DEV,
                        contactPoints = listOf("127.0.0.1"),
                        username = "cluster-user",
                        password = "cluster-password",
                        grantDbaToSuperAdmin = true,
                    ),
            )
        val service =
            InitialManagedClusterRegistrationService(
                properties = properties,
                connectionService =
                    ClusterConnectionService(
                        StaticManagedClusterProbeClientFactory(
                            mapOf(
                                "release_version" to "4.1.0",
                                "keyspace_name" to "system",
                                "keyspace_count" to 5L,
                                "table_count" to 12L,
                            ),
                        ),
                    ),
                encryptionService = AesGcmEncryptionService(properties),
                healthChecker = ClusterHealthChecker(),
                repository = ManagedClusterRepository(testConfigProvider(), executor),
            )

        val result = service.registerIfConfigured()

        assertTrue(result.registered)
        assertTrue(executor.queried.any { it.contains("managed_clusters_by_name") })
        assertTrue(
            executor.executed.any {
                it.contains("INSERT INTO cassdio_meta.managed_clusters") &&
                    it.contains("'Local Dev'")
            },
        )
        assertTrue(executor.executed.any { it.contains("INSERT INTO cassdio_meta.managed_cluster_credentials") })
        assertTrue(executor.executed.none { it.contains("cluster-password") })
        assertTrue(
            executor.executed.any {
                it.contains("INSERT INTO cassdio_meta.managed_cluster_health_snapshots") &&
                    it.contains("'HEALTHY'")
            },
        )
        assertTrue(
            executor.executed.any {
                it.contains("INSERT INTO cassdio_meta.role_assignments") &&
                    it.contains("'CLUSTER'")
            },
        )
        assertTrue(
            executor.executed.any {
                it.contains("INSERT INTO cassdio_meta.audit_logs") &&
                    it.contains("'INITIAL_CLUSTER_REGISTERED'")
            },
        )
    }

    @Test
    fun `initial managed cluster registration skips when cluster name already exists`() {
        val executor =
            RecordingCqlExecutor(
                queryHandler = { statement ->
                    when {
                        statement.contains("managed_clusters_by_name") -> CqlRow(mapOf("cluster_id" to "00000000-0000-0000-0000-000000000001"))
                        else -> null
                    }
                },
            )
        val service =
            InitialManagedClusterRegistrationService(
                properties =
                    MetadataBootstrapProperties(
                        initialCluster =
                            InitialManagedClusterProperties(
                                enabled = true,
                                name = "Local Dev",
                            ),
                    ),
                connectionService = ClusterConnectionService(StaticManagedClusterProbeClientFactory(emptyMap())),
                encryptionService = AesGcmEncryptionService(MetadataBootstrapProperties()),
                healthChecker = ClusterHealthChecker(),
                repository = ManagedClusterRepository(testConfigProvider(), executor),
            )

        val result = service.registerIfConfigured()

        assertFalse(result.registered)
        assertEquals(null, result.clusterId)
        assertTrue(result.message.contains("already exists"))
        assertEquals(1, executor.queried.size)
        assertTrue(executor.executed.isEmpty())
    }

    @Test
    fun `initial managed cluster registration fails before storing metadata when connection check fails`() {
        val executor = RecordingCqlExecutor()
        val service =
            InitialManagedClusterRegistrationService(
                properties =
                    MetadataBootstrapProperties(
                        initialCluster =
                            InitialManagedClusterProperties(
                                enabled = true,
                                name = "Broken Cluster",
                            ),
                    ),
                connectionService = ClusterConnectionService(StaticManagedClusterProbeClientFactory(emptyMap())),
                encryptionService = AesGcmEncryptionService(MetadataBootstrapProperties()),
                healthChecker = ClusterHealthChecker(),
                repository = ManagedClusterRepository(testConfigProvider(), executor),
            )

        val error =
            assertFailsWith<MetadataBootstrapException> {
                service.registerIfConfigured()
            }

        assertTrue(error.message.orEmpty().contains("Initial managed cluster connection failed"))
        assertTrue(executor.executed.isEmpty())
    }

    @Test
    fun `initial managed cluster registration can store cluster without dba assignment`() {
        val executor = RecordingCqlExecutor()
        val properties =
            MetadataBootstrapProperties(
                encryption = MetadataEncryptionProperties(masterKey = "test-master-key"),
                initialCluster =
                    InitialManagedClusterProperties(
                        enabled = true,
                        name = "Read Only Cluster",
                        environment = ManagedClusterEnvironment.STAGING,
                        contactPoints = listOf("127.0.0.1"),
                        username = "cluster-user",
                        password = "cluster-password",
                        grantDbaToSuperAdmin = false,
                    ),
            )
        val service =
            InitialManagedClusterRegistrationService(
                properties = properties,
                connectionService =
                    ClusterConnectionService(
                        StaticManagedClusterProbeClientFactory(
                            mapOf(
                                "release_version" to "4.0.0",
                                "keyspace_name" to "system",
                                "keyspace_count" to 3L,
                                "table_count" to 7L,
                            ),
                        ),
                    ),
                encryptionService = AesGcmEncryptionService(properties),
                healthChecker = ClusterHealthChecker(),
                repository = ManagedClusterRepository(testConfigProvider(), executor),
            )

        val result = service.registerIfConfigured()

        assertTrue(result.registered)
        assertTrue(executor.executed.any { it.contains("INSERT INTO cassdio_meta.managed_clusters") })
        assertTrue(executor.executed.any { it.contains("INSERT INTO cassdio_meta.managed_cluster_credentials") })
        assertFalse(
            executor.executed.any {
                it.contains("INSERT INTO cassdio_meta.role_assignments") &&
                    it.contains("'CLUSTER'")
            },
        )
        assertTrue(
            executor.executed.any {
                it.contains("INSERT INTO cassdio_meta.audit_logs") &&
                    it.contains("'dba_granted': 'false'")
            },
        )
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
    private val executeHandler: (String) -> Unit = {},
) : CqlExecutor {
    val executed = mutableListOf<String>()
    val queried = mutableListOf<String>()
    val events = mutableListOf<String>()

    override fun execute(statement: String) {
        executed += statement
        events += statement
        executeHandler(statement)
    }

    override fun queryOne(statement: String): CqlRow? {
        queried += statement
        events += statement
        return queryHandler(statement)
    }
}

private class StaticSeedCatalog(
    private val seeds: List<SeedDefinition>,
) : MetadataSeedCatalog() {
    override fun seeds(): List<SeedDefinition> = seeds
}

private class StaticPasswordHashService(
    private val hash: String,
) : PasswordHashService {
    override fun hash(rawPassword: String): String = hash
}

private class StaticManagedClusterProbeClientFactory(
    private val values: Map<String, Any?>,
) : ManagedClusterProbeClientFactory {
    override fun create(request: ManagedClusterRegistrationRequest): ManagedClusterProbeClient =
        object : ManagedClusterProbeClient {
            override fun queryOne(statement: String): CqlRow? =
                when {
                    statement.contains("system.local") -> CqlRow(mapOf("release_version" to values["release_version"]))
                    statement.contains("system_schema.keyspaces LIMIT") -> CqlRow(mapOf("keyspace_name" to values["keyspace_name"]))
                    statement.contains("keyspace_count") -> CqlRow(mapOf("keyspace_count" to values["keyspace_count"]))
                    statement.contains("table_count") -> CqlRow(mapOf("table_count" to values["table_count"]))
                    else -> null
                }

            override fun close() = Unit
        }
}
