package kr.hakdang.cadio.core.domain.cluster.keyspace.table;

import kr.hakdang.cadio.core.domain.cluster.keyspace.table.column.Column;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

/**
 * ClusterTableListResult
 *
 * @author seungh0
 * @since 2024-07-01
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClusterTableListResult {

    private List<ClusterTable> tables;
    private String nextPageState;

    public ClusterTableListResult(List<ClusterTable> tables, String nextPageState) {
        this.tables = tables;
        this.nextPageState = nextPageState;
    }

    public static ClusterTableListResult of(Map<ClusterTable, List<Column>> tables, ByteBuffer pagingState) {
        return new ClusterTableListResult(tables.keySet().stream().toList(), pagingState == null ? null : pagingState.toString());
    }

}
