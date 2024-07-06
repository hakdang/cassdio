package kr.hakdang.cassdio;

import com.datastax.oss.driver.api.core.CqlSession;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.InetSocketAddress;

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

    //    TODO : 샘플코드
    protected CqlSession makeSession() {
        return CqlSession.builder()
            .addContactPoint(new InetSocketAddress("127.0.0.1", 29042))
            .withLocalDatacenter("dc1")
            .build();
    }
}
