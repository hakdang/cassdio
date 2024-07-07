package kr.hakdang.cassdio.web.route.cluster.keyspace;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.internal.core.metadata.schema.queries.KeyspaceFilter;
import kr.hakdang.cassdio.core.domain.cluster.ClusterUtils;
import kr.hakdang.cassdio.core.domain.cluster.TempClusterConnector;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.ClusterKeyspaceCommander;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.ClusterKeyspaceListResult;
import kr.hakdang.cassdio.core.support.cache.CacheType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Map<String, List<String>> getKeyspaceNames(String clusterId) {
        try (CqlSession session = tempClusterConnector.makeSession(clusterId)) {

            List<String> keyspaceNames = clusterKeyspaceCommander.allKeyspaceNames(session);

            KeyspaceFilter keyspaceFilter = ClusterUtils.makeKeyspaceFilter(session.getContext());

            Map<String, List<String>> resultMap = new HashMap<>();
            for (String keyspaceName : keyspaceNames) {
                String keyName = keyspaceFilter.includes(keyspaceName) ?
                    "GENERAL" : //사용자 생성
                    "SYSTEM";

                List<String> list = resultMap.getOrDefault(keyName, new ArrayList<>());
                list.add(keyspaceName);
                resultMap.put(keyName, list);
            }

            return resultMap;
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
