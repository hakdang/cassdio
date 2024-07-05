package kr.hakdang.cadio.core.domain.cluster.keyspace.udt;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * ClusterUDTTypeArgs
 *
 * @author seungh0
 * @since 2024-07-06
 */
public class ClusterUDTTypeArgs {

    @ToString
    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ClusterUDTTypeListArgs {

        private String keyspace;
        private int pageSize = 50;
        private String nextPageState;

        @Builder
        private ClusterUDTTypeListArgs(String keyspace, int pageSize, String nextPageState) {
            this.keyspace = keyspace;
            this.pageSize = pageSize;
            this.nextPageState = nextPageState;
        }

    }

}
