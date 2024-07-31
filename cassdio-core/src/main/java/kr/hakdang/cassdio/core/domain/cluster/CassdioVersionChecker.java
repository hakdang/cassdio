package kr.hakdang.cassdio.core.domain.cluster;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.Version;
import kr.hakdang.cassdio.common.error.NotSupportedCassandraVersionException;
import org.springframework.stereotype.Service;

/**
 * CassdioVersionChecker
 *
 * @author seungh0
 * @since 2024-07-31
 */
@Service
public class CassdioVersionChecker {

    private static final Version MIN_SUPPORT_VERSION = Version.V3_0_0;

    private final ClusterVersionEvaluator clusterVersionEvaluator;
    private final ClusterConnector clusterConnector;

    public CassdioVersionChecker(ClusterVersionEvaluator clusterVersionEvaluator, ClusterConnector clusterConnector) {
        this.clusterVersionEvaluator = clusterVersionEvaluator;
        this.clusterConnector = clusterConnector;
    }

    public void verifyCompatibilityCassandraVersion(ClusterConnection connection) {
        try (CqlSession session = clusterConnector.makeSession(connection)) {
            if (clusterVersionEvaluator.isLessThan(session, MIN_SUPPORT_VERSION)) {
                throw new NotSupportedCassandraVersionException("Not supported cassandra with cluster version");
            }
        }
    }

}
