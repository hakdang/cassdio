package kr.hakdang.cadio.core.domain.cluster.keyspace.table;

import kr.hakdang.cadio.core.domain.cluster.keyspace.table.column.Column;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * ClusterTableGetResult
 *
 * @author seungh0
 * @since 2024-07-02
 */
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClusterTableGetResult2 {

    private Map<String, Object> table;
    private Map<String, Object> describe;

    @Builder
    private ClusterTableGetResult2(Map<String, Object> table, Map<String, Object> describe, List<Column> columns) {
        this.table = table;
        this.describe = describe;
    }

}
