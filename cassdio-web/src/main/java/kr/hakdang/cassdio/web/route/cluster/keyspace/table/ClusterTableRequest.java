package kr.hakdang.cassdio.web.route.cluster.keyspace.table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ClusterTableRequest
 *
 * @author akageun
 * @since 2024-08-19
 */
public class ClusterTableRequest {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TableRowImportRequest {
        private int perCommitSize;
        private String batchTypeCode;

        @Builder
        public TableRowImportRequest(int perCommitSize, String batchTypeCode) {
            this.perCommitSize = perCommitSize;
            this.batchTypeCode = batchTypeCode;
        }
    }
}
