package kr.hakdang.cassdio.core.domain.cluster;

import com.datastax.oss.driver.api.core.CqlSession;
import kr.hakdang.cassdio.core.domain.cluster.info.ClusterInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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

    private static final ConcurrentMap<String, CqlSession> SESSION = new ConcurrentHashMap<>();

    public CqlSessionFactory(ClusterProvider clusterProvider, ClusterConnector clusterConnector) {
        this.clusterProvider = clusterProvider;
        this.clusterConnector = clusterConnector;
    }

    public CqlSession get(String clusterId) {
        String key = String.format("%s", clusterId);
        return SESSION.computeIfAbsent(key, this::makeSession);
    }

    public void clearAll() {
        for (Map.Entry<String, CqlSession> entry : SESSION.entrySet()) {
            try {
                SESSION.remove(entry.getKey()).close();
            } catch (Exception e) {
                log.error("closed Fail : {}", entry.getKey());
            }
        }
    }

    public void clear(String clusterId) {
        try {
            CqlSession session = SESSION.remove(clusterId);
            if (session == null) {
                return;
            }

            session.close();
        } catch (Exception e) {
            log.error("closed Fail : {}", clusterId);
        }
    }

    public CqlSession makeSession(String clusterId) {
        ClusterInfo info = clusterProvider.findById(clusterId);
        if (info == null) {
            throw new IllegalArgumentException(String.format("failed to load Cluster(%s)", clusterId));
        }
        return clusterConnector.makeSession(info.makeClusterConnector());
    }
}
