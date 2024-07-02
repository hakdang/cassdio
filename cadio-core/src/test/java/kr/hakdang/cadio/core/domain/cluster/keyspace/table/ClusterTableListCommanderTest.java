package kr.hakdang.cadio.core.domain.cluster.keyspace.table;

import kr.hakdang.cadio.IntegrationTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * ClusterTableListCommanderTest
 *
 * @author seungh0
 * @since 2024-07-01
 */
@Slf4j
public class ClusterTableListCommanderTest extends IntegrationTest {

    @Autowired
    private ClusterTableListCommander clusterTableListCommander;

    @Test
    void listTables() {
        // given
        ClusterTableListResult sut = clusterTableListCommander.listTables(makeSession(), ClusterTableArgs.ClusterTableListArgs.builder()
            .keyspace("demodb")
            .limit(50)
            .build());
    }

}
