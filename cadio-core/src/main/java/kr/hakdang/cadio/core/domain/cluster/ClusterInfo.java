package kr.hakdang.cadio.core.domain.cluster;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ClusterInfo
 *
 * @author akageun
 * @since 2024-07-02
 */
@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClusterInfo {
    private String contactPoints;
    private int port;
    private String localDatacenter;
    private String username;
    private String password;

    @Builder
    public ClusterInfo(String contactPoints, int port, String localDatacenter, String username, String password) {
        //TODO : Validation

        this.contactPoints = contactPoints;
        this.port = port;
        this.localDatacenter = localDatacenter;
        this.username = username;
        this.password = password; //TODO : 암호화
    }
}
