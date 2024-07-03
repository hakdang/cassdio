package kr.hakdang.cadio.core.domain.cluster.keyspace.table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * ClusterTablePureSelectResult
 *
 * @author akageun
 * @since 2024-06-30
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClusterTablePureSelectResult {
    private boolean wasApplied;
    private List<String> columnNames;
    private List<Map<String, Object>> rows;
    private String previewToken;
    private String nextToken;

    @Builder
    public ClusterTablePureSelectResult(boolean wasApplied, List<String> columnNames, List<Map<String, Object>> rows, String previewToken, String nextToken) {
        this.wasApplied = wasApplied;
        this.columnNames = columnNames;
        this.rows = rows;
        this.previewToken = previewToken;
        this.nextToken = nextToken;
    }
}
