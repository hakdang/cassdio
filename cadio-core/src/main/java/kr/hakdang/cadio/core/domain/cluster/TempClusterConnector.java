package kr.hakdang.cadio.core.domain.cluster;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import kr.hakdang.cadio.core.domain.cluster.info.ClusterInfo;
import kr.hakdang.cadio.core.domain.cluster.info.ClusterInfoProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * TempClusterConnector
 * - 임시 목적으로 사용할 connector
 *
 * @author akageun
 * @since 2024-07-03
 */
@Slf4j
@Service
public class TempClusterConnector {

    private final ClusterInfoProvider clusterInfoProvider;

    public TempClusterConnector(
        ClusterInfoProvider clusterInfoProvider
    ) {
        this.clusterInfoProvider = clusterInfoProvider;
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
        CqlSessionBuilder builder = CqlSession.builder()
            .addContactPoints(makeContactPoint(clusterConnection.getContactPoints(), clusterConnection.getPort()))
            .withLocalDatacenter(clusterConnection.getLocalDatacenter());

        if (clusterConnection.isAuthCredentials()) {
            builder.withAuthCredentials(clusterConnection.getUsername(), clusterConnection.getPassword());
        }

        return builder.build();
    }

    public CqlSession makeSession(String clusterId) {
        ClusterInfo info = clusterInfoProvider.findClusterInfo(clusterId);
        return makeSession(info.makeClusterConnector());
    }
}
