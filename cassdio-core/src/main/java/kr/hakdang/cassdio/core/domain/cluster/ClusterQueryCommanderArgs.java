package kr.hakdang.cassdio.core.domain.cluster;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ClusterQueryCommanderArgs
 *
 * @author akageun
 * @since 2024-07-03
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClusterQueryCommanderArgs {
    private String query;
    private String cursor;
    private int pageSize;
    private int timeoutSeconds;
    private boolean trace = false;

    @Builder
    public ClusterQueryCommanderArgs(String query, String cursor, Integer pageSize, Integer timeoutSeconds, boolean trace) {
        if (pageSize == null || pageSize <= 0) {
            pageSize = 50;
        }

        if (pageSize > 500) {
            throw new RuntimeException("pageSize 500 over");
        }

        if (timeoutSeconds == null || timeoutSeconds <= 0) {
            timeoutSeconds = 3; //default 3
        }

        if (timeoutSeconds > 60) {
            throw new RuntimeException("timeout 60 over");
        }

        this.query = query;
        this.cursor = cursor;
        this.pageSize = pageSize;
        this.timeoutSeconds = timeoutSeconds;
        this.trace = trace;
    }
}
