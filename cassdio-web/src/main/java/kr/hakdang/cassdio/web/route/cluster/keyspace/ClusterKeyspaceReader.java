package kr.hakdang.cassdio.web.route.cluster.keyspace;

import com.datastax.oss.driver.api.core.CqlSession;
import kr.hakdang.cassdio.core.domain.cluster.TempClusterConnector;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.ClusterKeyspaceCommander;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.ClusterKeyspaceListResult;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.KeyspaceNameResult;
import kr.hakdang.cassdio.core.support.cache.CacheType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

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

    //TODO: 리네임
    public List<KeyspaceNameResult> allKeyspaceNameList(String clusterId) {
        try (CqlSession session = tempClusterConnector.makeSession(clusterId)) {

            List<KeyspaceNameResult> allKeyspaceList = clusterKeyspaceCommander.allKeyspaceList(session);

            return allKeyspaceList;
        }
    }

    @Cacheable(value = CacheType.CacheTypeNames.CLUSTER_LIST)
    public ClusterKeyspaceListResult listKeyspace(String clusterId) {
        try (CqlSession session = tempClusterConnector.makeSession(clusterId)) {
            return clusterKeyspaceCommander.generalKeyspaceList(session);
        }
    }

    @CacheEvict(value = CacheType.CacheTypeNames.CLUSTER_LIST)
    public void refreshKeyspaceCache(String clusterId) {
        log.info("ClusterList ({}) is evicted", clusterId);
    }

}
