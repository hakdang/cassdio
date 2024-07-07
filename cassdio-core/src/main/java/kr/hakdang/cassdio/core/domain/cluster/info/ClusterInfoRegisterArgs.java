package kr.hakdang.cassdio.core.domain.cluster.info;

import io.micrometer.common.util.StringUtils;
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
public class ClusterInfoRegisterArgs {
    private String clusterName;
    private String contactPoints;
    private int port;
    private String localDatacenter;
    private String username;
    private String password;

    //Append

    @Builder
    public ClusterInfoRegisterArgs(String clusterName, String contactPoints, int port, String localDatacenter, String username, String password) {
        if (StringUtils.isBlank(clusterName)) {
            throw new IllegalArgumentException("Cluster name is blank");
        }

        if (StringUtils.isBlank(contactPoints)) {
            throw new IllegalArgumentException("Contact points is blank");
        }

        if (port <= 0) {
            throw new IllegalArgumentException("Port is less than 0");
        }

        if (StringUtils.isBlank(localDatacenter)) {
            throw new IllegalArgumentException("Local datacenter is blank");
        }

        this.clusterName = clusterName;
        this.contactPoints = contactPoints;
        this.port = port;
        this.localDatacenter = localDatacenter;
        this.username = username;
        this.password = password;
    }

    public ClusterInfo makeClusterInfo(String clusterId) {
        return ClusterInfo.builder()
            .clusterId(clusterId)
            .clusterName(clusterName)
            .contactPoints(contactPoints)
            .port(port)
            .localDatacenter(localDatacenter)
            .username(username)
            .password(password)
            .build();
    }

}
