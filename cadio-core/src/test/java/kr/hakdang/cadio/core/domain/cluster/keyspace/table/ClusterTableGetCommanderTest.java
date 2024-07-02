package kr.hakdang.cadio.core.domain.cluster.keyspace.table;

import kr.hakdang.cadio.IntegrationTest;
import kr.hakdang.cadio.core.domain.cluster.keyspace.table.ClusterTableArgs.ClusterTableGetArgs;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * ClusterTableGetCommanderTest
 *
 * @author seungh0
 * @since 2024-07-02
 */
@Slf4j
class ClusterTableGetCommanderTest extends IntegrationTest {

    @Autowired
    private ClusterTableGetCommander clusterTableGetCommander;

    @Test
    void getTable() {
        // when
        ClusterTableGetResult sut = clusterTableGetCommander.getTable(makeSession(), ClusterTableGetArgs.builder()
            .keyspace("demodb")
            .table("event_history_v1")
            .build());

        log.info("{}", sut);
    }

}
