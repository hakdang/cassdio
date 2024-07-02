package kr.hakdang.cadio;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import com.datastax.oss.driver.api.querybuilder.schema.CreateKeyspace;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;

import java.net.InetSocketAddress;

/**
 * IntegrationTestDatabaseInitializer
 *
 * @author seungh0
 * @since 2024-07-02
 */
@Slf4j
@TestConfiguration
public class IntegrationTestDatabaseInitializer extends BaseTest {

    @Value("${cadio.test-cassandra.keyspace}")
    private String keyspaceName;

    @PostConstruct
    public void initialize() {
        try (CqlSession session = CqlSession.builder()
            .addContactPoint(new InetSocketAddress("127.0.0.1", 29042))
            .withLocalDatacenter("dc1")
            .build()) {
            CreateKeyspace createKeyspace = SchemaBuilder.createKeyspace(keyspaceName)
                .ifNotExists()
                .withSimpleStrategy(1);

            session.execute(createKeyspace.build());

            SimpleStatement createTable1 = SchemaBuilder.createTable("test_table_1")
                .withPartitionKey("partition_key_1", DataTypes.TEXT)
                .withPartitionKey("partition_key_2", DataTypes.TEXT)
                .withClusteringColumn("clustering_key_1", DataTypes.BIGINT)
                .withClusteringColumn("clustering_key_2", DataTypes.BIGINT)
                .withColumn("column_1", DataTypes.TEXT)
                .withComment("test_table_one")
                .withBloomFilterFpChance(0.01)
                .build()
                .setKeyspace(keyspaceName);
            session.execute(createTable1);

            SimpleStatement createTable2 = SchemaBuilder.createTable("test_table_2")
                .withPartitionKey("partition_key_11", DataTypes.TEXT)
                .withPartitionKey("partition_key_12", DataTypes.TEXT)
                .withClusteringColumn("clustering_key_11", DataTypes.BIGINT)
                .withClusteringColumn("clustering_key_12", DataTypes.BIGINT)
                .withColumn("column_11", DataTypes.TEXT)
                .withComment("test_table_two")
                .withBloomFilterFpChance(0.001)
                .build()
                .setKeyspace(keyspaceName);
            session.execute(createTable2);
        }

        log.info("Database initialization complete.....");
    }

    @PreDestroy
    void destroy() {
        try (CqlSession session = CqlSession.builder()
            .addContactPoint(new InetSocketAddress("127.0.0.1", 29042))
            .withLocalDatacenter("dc1")
            .build()) {
            session.execute(SchemaBuilder.dropKeyspace(keyspaceName)
                .ifExists()
                .build()
            );
        }

        log.info("Database initialization complete.....");
    }

}
