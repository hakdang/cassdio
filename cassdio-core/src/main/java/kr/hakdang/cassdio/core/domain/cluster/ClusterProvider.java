package kr.hakdang.cassdio.core.domain.cluster;

import kr.hakdang.cassdio.common.utils.IdGenerator;
import kr.hakdang.cassdio.core.domain.cluster.info.ClusterInfo;
import kr.hakdang.cassdio.core.domain.cluster.info.ClusterInfoArgs;
import kr.hakdang.cassdio.core.domain.cluster.info.ClusterManager;
import kr.hakdang.cassdio.core.support.cache.CacheType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClusterProvider
 *
 * @author akageun
 * @since 2024-07-25
 */
@Slf4j
@Service
public class ClusterProvider {

    private final ClusterManager clusterManager;
    private final ClusterNameCommander clusterNameCommander;

    public ClusterProvider(ClusterManager clusterManager, ClusterNameCommander clusterNameCommander) {
        this.clusterManager = clusterManager;
        this.clusterNameCommander = clusterNameCommander;
    }

    public List<ClusterInfo> findAll() {
        return clusterManager.findAll();
    }

    public List<ClusterInfo> findAllWithoutCache() {
        return clusterManager.findAll();
    }

    @Cacheable(cacheNames = CacheType.CacheTypeNames.CLUSTER_DETAIL, key = "#clusterId")
    public ClusterInfo findById(String clusterId) {
        return clusterManager.findById(clusterId);
    }

    public ClusterInfo findByIdWithoutCache(String clusterId) {
        return clusterManager.findById(clusterId);
    }

    public void register(ClusterInfoArgs args) {
        String clusterId = IdGenerator.makeId();
        String clusterName = clusterNameCommander.getClusterName(args.makeClusterConnector());

        clusterManager.register(args.makeClusterInfo(clusterId, clusterName));
    }

    @CacheEvict(cacheNames = CacheType.CacheTypeNames.CLUSTER_DETAIL, key = "#clusterId")
    public void updateById(String clusterId, ClusterInfoArgs args) {
        String clusterName = clusterNameCommander.getClusterName(args.makeClusterConnector());
        clusterManager.update(clusterId, args.makeClusterInfo(clusterId, clusterName));
    }

    @CacheEvict(cacheNames = CacheType.CacheTypeNames.CLUSTER_DETAIL, key = "#clusterId")
    public void deleteById(String clusterId) {
        clusterManager.deleteById(clusterId);
    }

}
