package kr.hakdang.cadio.core.domain.cluster.info;

import kr.hakdang.cadio.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class ClusterUtilsTest extends BaseTest {

    @Test
    void generateClusterIdTest() {
        for (int i = 0; i < 10; i++) {
            log.info("id : {}", ClusterUtils.generateClusterId());
        }
    }
}
