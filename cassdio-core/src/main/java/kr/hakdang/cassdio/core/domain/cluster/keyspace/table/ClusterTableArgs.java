package kr.hakdang.cassdio.core.domain.cluster.keyspace.table;

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
        private boolean withTableDescribe = false;

        @Builder
        private ClusterTableGetArgs(String keyspace, String table, boolean withTableDescribe) {
            this.keyspace = keyspace;
            this.table = table;
            this.withTableDescribe = withTableDescribe;
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

    @ToString
    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ClusterTablePureSelectArgs {

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

}
