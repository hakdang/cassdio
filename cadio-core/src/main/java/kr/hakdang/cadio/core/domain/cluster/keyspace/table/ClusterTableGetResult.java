package kr.hakdang.cadio.core.domain.cluster.keyspace.table;

import kr.hakdang.cadio.core.domain.cluster.keyspace.table.column.Column;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * ClusterTableGetResult
 *
 * @author seungh0
 * @since 2024-07-02
 */
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClusterTableGetResult {

    private ClusterTable table;
    private List<Column> columns;

    @Builder
    private ClusterTableGetResult(ClusterTable table, List<Column> columns) {
        this.table = table;
        this.columns = columns;
    }

}
