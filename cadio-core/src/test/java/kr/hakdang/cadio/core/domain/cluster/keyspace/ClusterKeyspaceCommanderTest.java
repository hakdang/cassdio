package kr.hakdang.cadio.core.domain.cluster.keyspace;

import com.datastax.oss.driver.api.core.CqlSession;
import kr.hakdang.cadio.IntegrationTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
class ClusterKeyspaceCommanderTest extends IntegrationTest {

    @Autowired
    private ClusterKeyspaceCommander clusterKeyspaceCommander;

    @Test
    void runTest() {
        try (CqlSession session = makeSession()) {
            log.info("describeResult : {}", clusterKeyspaceCommander.describe(session, ClusterKeyspaceDescribeArgs.builder()
                .keyspace("demodb")
                .withChildren(true)
                .pretty(true)
                .build()));
        }
    }
}
