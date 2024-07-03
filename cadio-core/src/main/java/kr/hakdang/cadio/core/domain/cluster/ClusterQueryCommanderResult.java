package kr.hakdang.cadio.core.domain.cluster;

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
    private List<String> columnNames;
    private List<Map<String, Object>> rows;
    private String previousCursor;
    private String nextCursor;

    @Builder
    public ClusterQueryCommanderResult(boolean wasApplied, List<String> columnNames, List<Map<String, Object>> rows, String previousCursor, String nextCursor) {
        this.wasApplied = wasApplied;
        this.columnNames = columnNames;
        this.rows = rows;
        this.previousCursor = previousCursor;
        this.nextCursor = nextCursor;
    }
}
