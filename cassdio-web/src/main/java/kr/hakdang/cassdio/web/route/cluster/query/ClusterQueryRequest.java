package kr.hakdang.cassdio.web.route.cluster.query;

import jakarta.validation.constraints.NotBlank;
import kr.hakdang.cassdio.core.domain.cluster.query.QueryDTO;
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
    @NotBlank
    private String query;
    private String cursor;
    private int pageSize;
    private int timeoutSeconds;
    private int consistencyLevelProtocolCode;
    private boolean trace = false;

    @Builder
    public ClusterQueryRequest(String query, String cursor, int pageSize, int timeoutSeconds, int consistencyLevelProtocolCode, boolean trace) {
        this.query = query;
        this.cursor = cursor;
        this.pageSize = pageSize;
        this.timeoutSeconds = timeoutSeconds;
        this.consistencyLevelProtocolCode = consistencyLevelProtocolCode;
        this.trace = trace;
    }

    public QueryDTO.ClusterQueryCommanderArgs makeArgs(String keyspace) {
        return QueryDTO.ClusterQueryCommanderArgs.builder()
            .keyspace(keyspace)
            .query(query)
            .cursor(cursor)
            .pageSize(pageSize)
            .timeoutSeconds(timeoutSeconds)
            .consistencyLevelProtocolCode(consistencyLevelProtocolCode)
            .trace(trace)
            .build();
    }
}
