package kr.hakdang.cassdio.core.domain.cluster.query;

import com.datastax.oss.driver.api.core.CqlSession;
import kr.hakdang.cassdio.IntegrationTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class ClusterQueryCommanderTest extends IntegrationTest {

    private final ClusterQueryCommander clusterQueryCommander;

    ClusterQueryCommanderTest(
        ClusterQueryCommander clusterQueryCommander
    ) {
        this.clusterQueryCommander = clusterQueryCommander;
    }

    @Value("${cassdio.test-cassandra.keyspace}")
    private String keyspaceName;

    private final static String TABLE_NAME = "test_table_1";

    @Test
    void queryTest() {
        //GIVEN
        QueryDTO.ClusterQueryCommanderArgs args = QueryDTO.ClusterQueryCommanderArgs.builder()
            .query(String.format("SELECT * FROM %s.%s", keyspaceName, TABLE_NAME))
            .build();

        try (CqlSession session = makeSession()) {

            //WHEN
            QueryDTO.ClusterQueryCommanderResult result = clusterQueryCommander.execute(session, args);

            //THEN
            assertThat(result).isNotNull();
            assertThat(result.getRows()).isNotNull();
        }

    }

    @Test
    void useKeyspaceTest() {
        //GIVEN
        QueryDTO.ClusterQueryCommanderArgs args = QueryDTO.ClusterQueryCommanderArgs.builder()
            .query(String.format("SELECT * FROM %s", TABLE_NAME))
            .keyspace(keyspaceName)
            .build();

        try (CqlSession session = makeSession()) {

            //WHEN
            QueryDTO.ClusterQueryCommanderResult result = clusterQueryCommander.execute(session, args);

            //THEN
            assertThat(result).isNotNull();
            assertThat(result.getRows()).isNotNull();
        }

    }
}
