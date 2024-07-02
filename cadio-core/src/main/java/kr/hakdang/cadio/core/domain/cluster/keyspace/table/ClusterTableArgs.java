package kr.hakdang.cadio.core.domain.cluster.keyspace.table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * ClusterTableArgs
 *
 * @author seungh0
 * @since 2024-07-02
 */
public class ClusterTableArgs {

    @ToString
    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ClusterTableGetArgs {

        private String keyspace;
        private String table;

        @Builder
        private ClusterTableGetArgs(String keyspace, String table) {
            this.keyspace = keyspace;
            this.table = table;
        }

    }

    @ToString
    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ClusterTableListArgs {

        private String keyspace;
        private int limit = 50;
        private String nextPageState;

        @Builder
        private ClusterTableListArgs(String keyspace, int limit, String nextPageState) {
            this.keyspace = keyspace;
            this.limit = limit;
            this.nextPageState = nextPageState;
        }

    }

}
