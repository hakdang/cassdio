package kr.hakdang.cassdio.web.route.cluster.keyspace.table;

import kr.hakdang.cassdio.core.domain.cluster.ClusterConnector;
import org.springframework.stereotype.Service;

/**
 * ClusterTableProvider
 *
 * @author seungh0
 * @since 2024-07-04
 */
@Service
public class ClusterTableProvider {

    private final ClusterConnector clusterConnector;

    public ClusterTableProvider(
        ClusterConnector clusterConnector
    ) {
        this.clusterConnector = clusterConnector;
    }

}
