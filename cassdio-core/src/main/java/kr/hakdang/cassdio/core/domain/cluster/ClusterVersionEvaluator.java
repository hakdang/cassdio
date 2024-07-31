package kr.hakdang.cassdio.core.domain.cluster;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.Version;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

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
        return isLessThan(() -> clusterVersionGetCommander.getCassandraVersion(clusterId), version);
    }

    public boolean isLessThanOrEqual(String clusterId, Version version) {
        return isLessThanOrEqual(() -> clusterVersionGetCommander.getCassandraVersion(clusterId), version);
    }

    public boolean isGreaterThan(String clusterId, Version version) {
        return isGreaterThan(() -> clusterVersionGetCommander.getCassandraVersion(clusterId), version);
    }

    public boolean isGreaterThanOrEqual(String clusterId, Version version) {
        return isGreaterThanOrEqual(() -> clusterVersionGetCommander.getCassandraVersion(clusterId), version);
    }

    public boolean isEqual(String clusterId, Version version) {
        return isEqual(() -> clusterVersionGetCommander.getCassandraVersion(clusterId), version);
    }

    public boolean isLessThan(CqlSession session, Version version) {
        return isLessThan(() -> clusterVersionGetCommander.getCassandraVersionWithSession(session), version);
    }

    public boolean isLessThanOrEqual(CqlSession session, Version version) {
        return isLessThanOrEqual(() -> clusterVersionGetCommander.getCassandraVersionWithSession(session), version);
    }

    public boolean isGreaterThan(CqlSession session, Version version) {
        return isGreaterThan(() -> clusterVersionGetCommander.getCassandraVersionWithSession(session), version);
    }

    public boolean isGreaterThanOrEqual(CqlSession session, Version version) {
        return isGreaterThanOrEqual(() -> clusterVersionGetCommander.getCassandraVersionWithSession(session), version);
    }

    public boolean isEqual(CqlSession session, Version version) {
        return isEqual(() -> clusterVersionGetCommander.getCassandraVersionWithSession(session), version);
    }

    private boolean isLessThan(Supplier<Version> versionConsumer, Version target) {
        return versionConsumer.get().compareTo(target) < 0;
    }

    private boolean isLessThanOrEqual(Supplier<Version> versionConsumer, Version target) {
        return versionConsumer.get().compareTo(target) <= 0;
    }

    private boolean isGreaterThan(Supplier<Version> versionConsumer, Version target) {
        return versionConsumer.get().compareTo(target) > 0;
    }

    private boolean isGreaterThanOrEqual(Supplier<Version> versionConsumer, Version target) {
        return versionConsumer.get().compareTo(target) >= 0;
    }

    private boolean isEqual(Supplier<Version> versionConsumer, Version target) {
        return versionConsumer.get().compareTo(target) == 0;
    }

}
