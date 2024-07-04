package kr.hakdang.cadio.web.route.cluster.keyspace;

import com.datastax.oss.driver.api.core.CqlSession;
import kr.hakdang.cadio.core.domain.cluster.TempClusterConnector;
import kr.hakdang.cadio.core.domain.cluster.keyspace.ClusterKeyspaceCommander;
import kr.hakdang.cadio.core.domain.cluster.keyspace.ClusterKeyspaceDescribeArgs;
import kr.hakdang.cadio.core.domain.cluster.keyspace.ClusterKeyspaceListResult;
import kr.hakdang.cadio.core.support.cache.CacheType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * ClusterKeyspaceReader
 *
 * @author seungh0
 * @since 2024-07-04
 */
@Slf4j
@Service
public class ClusterKeyspaceReader {

    private final TempClusterConnector tempClusterConnector;
    private final ClusterKeyspaceCommander clusterKeyspaceCommander;

    public ClusterKeyspaceReader(
        TempClusterConnector tempClusterConnector,
        ClusterKeyspaceCommander clusterKeyspaceCommander
    ) {
        this.tempClusterConnector = tempClusterConnector;
        this.clusterKeyspaceCommander = clusterKeyspaceCommander;
    }

    @Cacheable(value = CacheType.CacheTypeNames.CLUSTER_LIST)
    public ClusterKeyspaceListResult listKeyspace(String clusterId) {
        try (CqlSession session = tempClusterConnector.makeSession(clusterId)) {
            return clusterKeyspaceCommander.keyspaceList(session);
        }
    }

    @CacheEvict(value = CacheType.CacheTypeNames.CLUSTER_LIST)
    public void refreshKeyspaceCache(String clusterId) {
        log.info("ClusterList ({}) is evicted", clusterId);
    }

    @Cacheable(value = CacheType.CacheTypeNames.CLUSTER_LIST)
    public String getKeyspace(String clusterId, String keyspace) {
        try (CqlSession session = tempClusterConnector.makeSession(clusterId)) {
            return clusterKeyspaceCommander.describe(session, ClusterKeyspaceDescribeArgs.builder()
                .keyspace(keyspace)
                .withChildren(false)
                .pretty(true)
                .build());
        }
    }

}
