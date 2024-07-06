package kr.hakdang.cassdio.core.domain.cluster.info;

import kr.hakdang.cassdio.core.domain.cluster.ClusterConnection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ClusterInfo
 *
 * @author akageun
 * @since 2024-07-03
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClusterInfo {
    private String clusterId;
    private String clusterName;
    private String contactPoints;
    private int port;
    private String localDatacenter;
    private String username;
    private String password;

    @Builder(toBuilder = true)
    public ClusterInfo(
        String clusterId,
        String clusterName,
        String contactPoints,
        int port,
        String localDatacenter,
        String username,
        String password
    ) {
        this.clusterId = clusterId;
        this.clusterName = clusterName;
        this.contactPoints = contactPoints;
        this.port = port;
        this.localDatacenter = localDatacenter;
        this.username = username;
        this.password = password;
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
