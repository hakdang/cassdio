package kr.hakdang.cassdio.core.domain.cluster;

import kr.hakdang.cassdio.BaseTest;
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
