package kr.hakdang.cassdio.core.domain.cluster;

import com.datastax.oss.driver.api.core.Version;
import org.springframework.stereotype.Component;

/**
 * ClusterVersionEvaluator
 *
 * @author seungh0
 * @since 2024-07-31
 */
@Component
public class ClusterVersionEvaluator {

    private final ClusterVersionGetCommander clusterVersionGetCommander;

    public ClusterVersionEvaluator(ClusterVersionGetCommander clusterVersionGetCommander) {
        this.clusterVersionGetCommander = clusterVersionGetCommander;
    }

    public boolean isLessThan(String clusterId, Version version) {
        return clusterVersionGetCommander.getCassandraVersion(clusterId).compareTo(version) < 0;
    }

    public boolean isLessThanOrEqual(String clusterId, Version version) {
        return clusterVersionGetCommander.getCassandraVersion(clusterId).compareTo(version) <= 0;
    }

    public boolean isGreaterThan(String clusterId, Version version) {
        return clusterVersionGetCommander.getCassandraVersion(clusterId).compareTo(version) > 0;
    }

    public boolean isGreaterThanOrEqual(String clusterId, Version version) {
        return clusterVersionGetCommander.getCassandraVersion(clusterId).compareTo(version) >= 0;
    }

    public boolean isEqual(String clusterId, Version version) {
        return clusterVersionGetCommander.getCassandraVersion(clusterId).compareTo(version) == 0;
    }

}
