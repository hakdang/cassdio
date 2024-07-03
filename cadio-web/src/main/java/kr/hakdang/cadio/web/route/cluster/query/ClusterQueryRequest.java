package kr.hakdang.cadio.web.route.cluster.query;

import kr.hakdang.cadio.core.domain.cluster.ClusterQueryCommanderArgs;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ClusterQueryRequest
 *
 * @author akageun
 * @since 2024-07-03
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClusterQueryRequest {
    private String query;
    private String cursor;
    private int pageSize;
    private int timeoutSeconds;
    private boolean trace = false;

    @Builder
    public ClusterQueryRequest(String query, String cursor, int pageSize, int timeoutSeconds, boolean trace) {
        this.query = query;
        this.cursor = cursor;
        this.pageSize = pageSize;
        this.timeoutSeconds = timeoutSeconds;
        this.trace = trace;
    }

    public ClusterQueryCommanderArgs makeArgs() {
        return ClusterQueryCommanderArgs.builder()
            .query(query)
            .cursor(cursor)
            .pageSize(pageSize)
            .timeoutSeconds(timeoutSeconds)
            .trace(trace)
            .build();
    }
}
