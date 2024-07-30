package kr.hakdang.cassdio.core.domain.cluster;

import com.datastax.oss.driver.api.core.CqlSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * CqlSessionFactory
 *
 * @author akageun
 * @since 2024-07-30
 */
@Slf4j
@Service
public class CqlSessionFactory {

    private final ClusterConnector clusterConnector;

    public static ConcurrentHashMap<String, CqlSession> SESSION = new ConcurrentHashMap<>();

    public CqlSessionFactory(ClusterConnector clusterConnector) {
        this.clusterConnector = clusterConnector;
    }


    public CqlSession get(String clusterId) {
        String key = String.format("%s", clusterId);
        CqlSession session = SESSION.get(key);
        if (session == null) {
            session = clusterConnector.makeSession(clusterId);
            SESSION.put(key, session);
        }

        return session;
    }
}
