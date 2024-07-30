package kr.hakdang.cassdio.core.domain.cluster;

import com.datastax.oss.driver.api.core.CqlSession;
import kr.hakdang.cassdio.common.utils.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * ClusterNameCommander
 *
 * @author akageun
 * @since 2024-07-30
 */
@Slf4j
@Service
public class ClusterNameCommander {

    private final ClusterConnector clusterConnector;

    public ClusterNameCommander(ClusterConnector clusterConnector) {
        this.clusterConnector = clusterConnector;
    }

    public String getClusterName(ClusterConnection connection) {
        try (CqlSession session = clusterConnector.makeSession(connection)) {
            return session.getMetadata().getClusterName()
                .orElse(UUID.randomUUID().toString());
        }
    }
}
