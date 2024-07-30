package kr.hakdang.cassdio.core.domain.cluster;

import com.datastax.oss.driver.api.core.CqlSession;
import kr.hakdang.cassdio.core.domain.cluster.info.ClusterInfo;
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

    private final ClusterProvider clusterProvider;
    private final ClusterConnector clusterConnector;

    public static ConcurrentHashMap<String, CqlSession> SESSION = new ConcurrentHashMap<>();

    public CqlSessionFactory(ClusterProvider clusterProvider, ClusterConnector clusterConnector) {
        this.clusterProvider = clusterProvider;
        this.clusterConnector = clusterConnector;
    }

    public CqlSession get(String clusterId) {
        String key = String.format("%s", clusterId);
        CqlSession session = SESSION.get(key);
        if (session == null) {
            session = makeSession(clusterId);
            SESSION.put(key, session);
        }

        return session;
    }

    public CqlSession makeSession(String clusterId) {
        ClusterInfo info = clusterProvider.findById(clusterId);
        if (info == null) {
            throw new IllegalArgumentException(String.format("failed to load Cluster(%s)", clusterId));
        }
        return clusterConnector.makeSession(info.makeClusterConnector());
    }
}
