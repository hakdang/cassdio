package kr.hakdang.cassdio;

import com.datastax.oss.driver.api.core.CqlSession;
import kr.hakdang.cassdio.core.domain.cluster.info.ClusterInfo;
import kr.hakdang.cassdio.core.domain.cluster.info.ClusterManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.net.InetSocketAddress;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

/**
 * IntegrationTest
 *
 * @author seungh0
 * @since 2024-07-01
 */
@Tag("integration-test")
@SpringBootTest(classes = {CoreRoot.class})
public abstract class IntegrationTest extends BaseTest {

    @Value("${cassdio.test-cassandra.keyspace}")
    protected String keyspaceName;

    @MockBean
    private ClusterManager clusterManager;

    @BeforeEach
    void setup() {
        given(clusterManager.findById(eq(CLUSTER_ID))).willReturn(ClusterInfo.builder()
            .contactPoints("127.0.0.1")
            .port(29042)
            .localDatacenter("dc1")
            .clusterId(CLUSTER_ID)
            .build());
    }

    //    TODO : 샘플코드
    protected CqlSession makeSession() {
        return CqlSession.builder()
            .addContactPoint(new InetSocketAddress("127.0.0.1", 29042))
            .withLocalDatacenter("dc1")
            .build();
    }

}
