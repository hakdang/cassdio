package kr.hakdang.cassdio.core.domain.cluster.keyspace.table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ClusterDescTablesArgs
 *
 * @author akageun
 * @since 2024-07-05
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClusterDescTablesArgs {
    private String keyspace;
    private int pageSize; //TODO : max check

    private String cursor;

    @Builder
    public ClusterDescTablesArgs(String keyspace, int pageSize, String cursor) {
        this.keyspace = keyspace;
        this.pageSize = pageSize;
        this.cursor = cursor;
    }
}
