package kr.hakdang.cadio.web.route.cluster;

import kr.hakdang.cadio.core.domain.cluster.ClusterConnection;
import kr.hakdang.cadio.core.domain.cluster.info.ClusterInfoRegisterArgs;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ClusterRegisterRequest
 *
 * @author akageun
 * @since 2024-07-03
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClusterRegisterRequest {
    private String contactPoints;
    private int port;
    private String localDatacenter;
    private String username;
    private String password;

    @Builder
    public ClusterRegisterRequest(String contactPoints, int port, String localDatacenter, String username, String password) {
        this.contactPoints = contactPoints;
        this.port = port;
        this.localDatacenter = localDatacenter;
        this.username = username;
        this.password = password;
    }

    public ClusterInfoRegisterArgs makeArgs(String clusterName) {
        return ClusterInfoRegisterArgs.builder()
            .clusterName(clusterName)
            .contactPoints(contactPoints)
            .port(port)
            .localDatacenter(localDatacenter)
            .username(username)
            .password(password)
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