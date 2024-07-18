package kr.hakdang.cassdio.core.domain.cluster;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.config.DefaultDriverOption;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import kr.hakdang.cassdio.core.domain.cluster.info.ClusterInfo;
import kr.hakdang.cassdio.core.domain.cluster.info.ClusterManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * ClusterConnector
 * - 임시 목적으로 사용할 connector
 *
 * @author akageun
 * @since 2024-07-03
 */
@Slf4j
@Service
public class ClusterConnector {

    private final ClusterManager clusterManager;

    public ClusterConnector(
        ClusterManager clusterManager
    ) {
        this.clusterManager = clusterManager;
    }

    public List<InetSocketAddress> makeContactPoint(String contactPoints, int port) {
        String[] contactPointArr = StringUtils.split(contactPoints, ",");

        List<InetSocketAddress> result = new ArrayList<>();
        for (String contactPoint : contactPointArr) {
            result.add(new InetSocketAddress(contactPoint, port));
        }

        return result;
    }

    public CqlSession makeSession(ClusterConnection clusterConnection) {
        return makeSession(clusterConnection, null);
    }

    public CqlSession makeSession(ClusterConnection clusterConnection, String keyspace) {
        CqlSessionBuilder builder = CqlSession.builder()
            .addContactPoints(makeContactPoint(clusterConnection.getContactPoints(), clusterConnection.getPort()))
            .withLocalDatacenter(clusterConnection.getLocalDatacenter());

        if (clusterConnection.isAuthCredentials()) {
            builder.withAuthCredentials(clusterConnection.getUsername(), clusterConnection.getPassword());
        }

        builder.withConfigLoader(
            DriverConfigLoader.programmaticBuilder()
                .withDuration(DefaultDriverOption.CONNECTION_INIT_QUERY_TIMEOUT, Duration.ofSeconds(5))
                .withDuration(DefaultDriverOption.REQUEST_TIMEOUT, Duration.ofSeconds(5))
                .build()
        );

        if (StringUtils.isNotBlank(keyspace)) {
            builder.withKeyspace(keyspace);
        }

        return builder.build();
    }

    public CqlSession makeSession(String clusterId) {
        ClusterInfo info = clusterManager.findById(clusterId);
        if (info == null) {
            throw new IllegalArgumentException(String.format("failed to load Cluster(%s)", clusterId));
        }
        return makeSession(info.makeClusterConnector(), null);
    }

    public CqlSession makeSession(String clusterId, String keyspace) {
        ClusterInfo info = clusterManager.findById(clusterId);
        if (info == null) {
            throw new IllegalArgumentException(String.format("failed to load Cluster(%s)", clusterId));
        }
        return makeSession(info.makeClusterConnector(), keyspace);
    }
}
