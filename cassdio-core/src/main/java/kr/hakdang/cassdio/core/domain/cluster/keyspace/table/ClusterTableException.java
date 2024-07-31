package kr.hakdang.cassdio.core.domain.cluster.keyspace.table;

import kr.hakdang.cassdio.common.error.BaseException;
import kr.hakdang.cassdio.common.error.ErrorCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * ClusterTableException
 *
 * @author seungh0
 * @since 2024-07-07
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClusterTableException {

    public static class ClusterTableNotFoundException extends BaseException {

        protected ClusterTableNotFoundException(String message) {
            super(message, ErrorCode.E404_NOT_FOUND_CLUSTER_NODE);
        }
    }

}
