package kr.hakdang.cadio.core.domain.cluster.keyspace.table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ClusterTableListArgs
 *
 * @author seungh0
 * @since 2024-07-01
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClusterTableListArgs {

    private String keyspace;
    private int limit = 50;
    private String nextPageState;

    @Builder
    public ClusterTableListArgs(String keyspace, int limit, String nextPageState) {
        this.keyspace = keyspace;
        this.limit = limit;
        this.nextPageState = nextPageState;
    }

}
