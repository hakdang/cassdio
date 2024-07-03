package kr.hakdang.cadio.core.domain.cluster;

import io.micrometer.common.util.StringUtils;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ClusterConnection
 *
 * @author akageun
 * @since 2024-07-02
 */
@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClusterConnection {
    private String contactPoints;
    private int port;
    private String localDatacenter;
    private String username;
    private String password;

    @Builder
    public ClusterConnection(String contactPoints, int port, String localDatacenter, String username, String password) {
        //TODO : Validation

        this.contactPoints = contactPoints;
        this.port = port;
        this.localDatacenter = localDatacenter;
        this.username = username;
        this.password = password; //TODO : 암호화
    }

    public boolean isAuthCredentials() {
        return StringUtils.isNotBlank(this.username) && StringUtils.isNotBlank(this.password);
    }
}
