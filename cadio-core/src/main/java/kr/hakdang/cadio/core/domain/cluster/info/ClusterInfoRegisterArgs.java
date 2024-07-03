package kr.hakdang.cadio.core.domain.cluster.info;

import kr.hakdang.cadio.core.domain.cluster.ClusterConnection;
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
