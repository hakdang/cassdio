package kr.hakdang.cassdio.core.domain.cluster.keyspace.udt;

import io.micrometer.common.util.StringUtils;
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
            if (StringUtils.isBlank(keyspace)) {
                throw new IllegalArgumentException("keyspace can't be null or empty");
            }

            if (pageSize > 100) {
                throw new IllegalArgumentException("pageSize can't be greater than 100");
            }

            this.keyspace = keyspace;
            this.pageSize = pageSize;
            this.nextPageState = nextPageState;
        }

    }

    @ToString
    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ClusterUDTTypeGetArgs {

        private String keyspace;
        private String type;

        @Builder
        public ClusterUDTTypeGetArgs(String keyspace, String type) {
            if (StringUtils.isBlank(keyspace)) {
                throw new IllegalArgumentException("keyspace can't be null or empty");
            }

            if (StringUtils.isBlank(type)) {
                throw new IllegalArgumentException("type can't be null or empty");
            }

            this.keyspace = keyspace;
            this.type = type;
        }

    }

}
