package kr.hakdang.cassdio.core.domain.cluster.info;

import io.micrometer.common.util.StringUtils;
import kr.hakdang.cassdio.core.domain.cluster.ClusterConnection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ClusterInfoRegisterArgs
 *
 * @author akageun
 * @since 2024-07-03
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClusterInfoArgs {
    private String contactPoints;
    private int port;
    private String localDatacenter;
    private String username;
    private String password;
    private String memo;

    //Append

    @Builder
    public ClusterInfoArgs(
        String contactPoints, int port, String localDatacenter, String username, String password, String memo
    ) {
//        if (StringUtils.isBlank(clusterName)) {
//            throw new IllegalArgumentException("Cluster name is blank");
//        }

        if (StringUtils.isBlank(contactPoints)) {
            throw new IllegalArgumentException("Contact points is blank");
        }

        if (port <= 0) {
            throw new IllegalArgumentException("Port is less than 0");
        }

        if (StringUtils.isBlank(localDatacenter)) {
            throw new IllegalArgumentException("Local datacenter is blank");
        }

        this.contactPoints = contactPoints;
        this.port = port;
        this.localDatacenter = localDatacenter;
        this.username = username;
        this.password = password;
        this.memo = memo;
    }

    public ClusterInfo makeClusterInfo(String clusterId, String clusterName) {
        return ClusterInfo.builder()
            .clusterId(clusterId)
            .clusterName(clusterName)
            .contactPoints(contactPoints)
            .port(port)
            .localDatacenter(localDatacenter)
            .username(username)
            .password(password)
            .memo(memo)
            .build();
    }

    public ClusterConnection makeClusterConnector() {
        return ClusterConnection.builder()
            .contactPoints(contactPoints)
            .port(port)
            .localDatacenter(localDatacenter)
            .username(username)
            .password(password)
            .build();
    }
}
