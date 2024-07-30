package kr.hakdang.cassdio.core.domain.cluster.keyspace;

import com.datastax.oss.driver.api.core.CqlSession;
import kr.hakdang.cassdio.core.support.cache.CacheType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClusterKeyspaceProvider
 *
 * @author akageun
 * @since 2024-07-25
 */
@Slf4j
@Service
public class ClusterKeyspaceProvider {

    private final ClusterKeyspaceCommander clusterKeyspaceCommander;

    public ClusterKeyspaceProvider(ClusterKeyspaceCommander clusterKeyspaceCommander) {
        this.clusterKeyspaceCommander = clusterKeyspaceCommander;

    }

    @Cacheable(cacheNames = CacheType.CacheTypeNames.CLUSTER_KEYSPACE_NAME_LIST, key = "#clusterId")
    public List<KeyspaceDTO.KeyspaceNameResult> keyspaceNameResultList(String clusterId) {
        return clusterKeyspaceCommander.allKeyspaceNameList(clusterId);
    }

    @CacheEvict(cacheNames = CacheType.CacheTypeNames.CLUSTER_KEYSPACE_NAME_LIST, key = "#clusterId")
    public void keyspaceNameListCacheEvict(String clusterId) {
        log.info("{}, cacheName : {}, Evict!!", clusterId, CacheType.CacheTypeNames.CLUSTER_KEYSPACE_NAME_LIST);
    }
}
