package kr.hakdang.cassdio.core.domain.cluster.info;

import io.micrometer.common.util.StringUtils;
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
    private String memo;

    @Builder(toBuilder = true)
    public ClusterInfo(
        String clusterId,
        String clusterName,
        String contactPoints,
        int port,
        String localDatacenter,
        String username,
        String password,
        String memo
    ) {
        if (StringUtils.isBlank(clusterId)) {
            throw new IllegalArgumentException("ClusterId is blank");
        }

        this.clusterId = clusterId;
        this.clusterName = clusterName;
        this.contactPoints = contactPoints;
        this.port = port;
        this.localDatacenter = localDatacenter;
        this.username = username;
        this.password = password;
        this.memo = memo;
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
