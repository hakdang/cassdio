package kr.hakdang.cadio.core.domain.cluster.keyspace;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;

@Slf4j
@SpringBootConfiguration
class ClusterKeyspaceDescribeCommanderTest {

    @Autowired
    private ClusterKeyspaceDescribeCommander clusterKeyspaceDescribeCommander;

    @Test
    void runTest() {
        ClusterKeyspaceDescribeResult describeResult = clusterKeyspaceDescribeCommander.execute(null, ClusterKeyspaceDescribeArgs.builder()
            .keyspace("demodb")
            .withChildren(true)
            .pretty(true)
            .build());

        log.info("describeResult : {}", describeResult);
    }
}
