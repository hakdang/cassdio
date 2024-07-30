package kr.hakdang.cassdio.core.domain.cluster.query;

import kr.hakdang.cassdio.IntegrationTest;
import kr.hakdang.cassdio.common.error.NotSupportedCassandraVersionException;
import kr.hakdang.cassdio.core.domain.cluster.CqlSessionFactory;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@Slf4j
class ClusterQueryCommanderTest extends IntegrationTest {

    private final ClusterQueryCommander clusterQueryCommander;

    ClusterQueryCommanderTest(
        ClusterQueryCommander clusterQueryCommander
    ) {
        this.clusterQueryCommander = clusterQueryCommander;
    }

    @MockBean
    private CqlSessionFactory cqlSessionFactory;

    @Value("${cassdio.test-cassandra.keyspace}")
    private String keyspaceName;

    private final static String TABLE_NAME = "test_table_1";

    @Test
    void queryTest() {
        //GIVEN
        given(cqlSessionFactory.get(anyString())).willReturn(makeSession());

        QueryDTO.ClusterQueryCommanderArgs args = QueryDTO.ClusterQueryCommanderArgs.builder()
            .query(String.format("SELECT * FROM %s.%s", keyspaceName, TABLE_NAME))
            .build();

        //WHEN
        QueryDTO.ClusterQueryCommanderResult result = clusterQueryCommander.execute(CLUSTER_ID, args);

        //THEN
        assertThat(result).isNotNull();
        assertThat(result.getRows()).isNotNull();

    }

    @Test
    void useKeyspaceTest() {
        //GIVEN
        given(cqlSessionFactory.get(anyString())).willReturn(makeSession());

        QueryDTO.ClusterQueryCommanderArgs args = QueryDTO.ClusterQueryCommanderArgs.builder()
            .query(String.format("SELECT * FROM %s", TABLE_NAME))
            .keyspace(keyspaceName)
            .build();

        //WHEN
        if (clusterQueryCommander.useKeyspaceQueryCommandNotSupport(CLUSTER_ID)) {
            assertThatThrownBy(() -> {
                    clusterQueryCommander.execute(CLUSTER_ID, args);
                }
            ).isInstanceOf(NotSupportedCassandraVersionException.class);

        } else {
            QueryDTO.ClusterQueryCommanderResult result = clusterQueryCommander.execute(CLUSTER_ID, args);

            //THEN
            assertThat(result).isNotNull();
            assertThat(result.getRows()).isNotNull();
        }
    }
}
