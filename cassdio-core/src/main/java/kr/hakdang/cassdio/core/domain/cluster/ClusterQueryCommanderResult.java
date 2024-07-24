package kr.hakdang.cassdio.core.domain.cluster;

import kr.hakdang.cassdio.core.domain.cluster.keyspace.CassdioColumnDefinition;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * ClusterQueryCommanderResult
 *
 * @author akageun
 * @since 2024-07-03
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClusterQueryCommanderResult {
    private boolean wasApplied;
    private List<CassdioColumnDefinition> rowHeader;
    private List<Map<String, Object>> rows;
    private String previousCursor;
    private String nextCursor;

    @Builder
    public ClusterQueryCommanderResult(boolean wasApplied, List<CassdioColumnDefinition> rowHeader, List<Map<String, Object>> rows, String previousCursor, String nextCursor) {
        this.wasApplied = wasApplied;
        this.rowHeader = rowHeader;
        this.rows = rows;
        this.previousCursor = previousCursor;
        this.nextCursor = nextCursor;
    }
}
