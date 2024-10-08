package kr.hakdang.cassdio;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder;
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

    @Value("${cassdio.test-cassandra.keyspace}")
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

            SimpleStatement createTable1 = SchemaBuilder.createTable(keyspaceName, "test_table_1")
                .ifNotExists()
                .withPartitionKey("partition_key_1", DataTypes.TEXT)
                .withPartitionKey("partition_key_2", DataTypes.BIGINT)
                .withClusteringColumn("clustering_key_1", DataTypes.BIGINT)
                .withClusteringColumn("clustering_key_2", DataTypes.TEXT)
                .withColumn("column_1", DataTypes.TEXT)
                .withClusteringOrder("clustering_key_1", ClusteringOrder.DESC)
                .withClusteringOrder("clustering_key_2", ClusteringOrder.ASC)
                .withComment("test_table_one")
                .withBloomFilterFpChance(0.01)
                .build();
            session.execute(createTable1);

            SimpleStatement createTable2 = SchemaBuilder.createTable(keyspaceName, "test_table_2")
                .ifNotExists()
                .withPartitionKey("partition_key_11", DataTypes.TEXT)
                .withPartitionKey("partition_key_12", DataTypes.BIGINT)
                .withClusteringColumn("clustering_key_11", DataTypes.BIGINT)
                .withClusteringColumn("clustering_key_12", DataTypes.TEXT)
                .withColumn("column_11", DataTypes.TEXT)
                .withComment("test_table_two")
                .withBloomFilterFpChance(0.001)
                .build();
            session.execute(createTable2);

            SimpleStatement createType = SchemaBuilder.createType(keyspaceName, "test_type_1")
                .ifNotExists()
                .withField("field_1", DataTypes.TEXT)
                .withField("field_2", DataTypes.BIGINT)
                .withField("field_3", DataTypes.TIME)
                .build();
            session.execute(createType);
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

        log.info("Database cleanup complete.....");
    }

}
