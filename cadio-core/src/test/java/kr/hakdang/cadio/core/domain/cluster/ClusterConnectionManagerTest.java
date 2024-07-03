package kr.hakdang.cadio.core.domain.cluster;

import kr.hakdang.cadio.IntegrationTest;
import kr.hakdang.cadio.core.domain.cluster.info.ClusterInfoManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
class ClusterConnectionManagerTest extends IntegrationTest {

    @Autowired
    private ClusterInfoManager clusterInfoManager;

    @Test
    void registerTest() {
       // clusterInfoManager.register("127.0.0.1", 9042, "dc1", "", "");
    }
}
