package kr.hakdang.cassdio.web.route.cluster;

import kr.hakdang.cassdio.core.domain.cluster.ClusterConnection;
import kr.hakdang.cassdio.core.domain.cluster.info.ClusterInfoArgs;
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

    public ClusterInfoArgs makeArgs(String clusterName) {
        return ClusterInfoArgs.builder()
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
