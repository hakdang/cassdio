package kr.hakdang.cadio.web.route;

import com.datastax.oss.driver.api.core.CqlSession;

import java.net.InetSocketAddress;

/**
 * BaseSample
 *
 * @author akageun
 * @since 2024-07-02
 */
public abstract class BaseSample {

    protected CqlSession makeSession() {
        return CqlSession.builder()
            .addContactPoint(new InetSocketAddress("127.0.0.1", 29042))
            .withLocalDatacenter("dc1")
            .build();
    }
}
