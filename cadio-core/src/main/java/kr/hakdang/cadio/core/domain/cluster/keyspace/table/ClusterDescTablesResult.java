package kr.hakdang.cadio.core.domain.cluster.keyspace.table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * ClusterDescTablesResult
 *
 * @author akageun
 * @since 2024-06-30
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClusterDescTablesResult {
    private boolean wasApplied;
    private List<String> columnNames;
    private List<Map<String, Object>> rows;
    private String nextCursor;

    @Builder
    public ClusterDescTablesResult(boolean wasApplied, List<String> columnNames, List<Map<String, Object>> rows, String nextCursor) {
        this.wasApplied = wasApplied;
        this.columnNames = columnNames;
        this.rows = rows;
        this.nextCursor = nextCursor;
    }
}
