package kr.hakdang.cadio.core.domain.cluster.keyspace.table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ClusterTablePureSelectArgs
 *
 * @author akageun
 * @since 2024-06-30
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClusterTablePureSelectArgs {

    private String keyspace;
    private String table;

    private int pageSize; //TODO : max check

    private String cursor;

    @Builder
    public ClusterTablePureSelectArgs(String keyspace, String table, int pageSize, String cursor) {
        this.keyspace = keyspace;
        this.table = table;
        this.pageSize = pageSize;
        this.cursor = cursor;
    }
}
