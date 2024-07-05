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
        private int pageSize = 50;
        private String nextPageState;

        @Builder
        private ClusterTableListArgs(String keyspace, int pageSize, String nextPageState) {
            this.keyspace = keyspace;
            this.pageSize = pageSize;
            this.nextPageState = nextPageState;
        }

    }

}
