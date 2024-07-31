package kr.hakdang.cassdio.core.domain.cluster;

import kr.hakdang.cassdio.common.error.BaseException;
import kr.hakdang.cassdio.common.error.ErrorCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * ClusterException
 *
 * @author seungh0
 * @since 2024-07-07
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClusterException {

    public static class ClusterNodeNotFoundException extends BaseException {

        public ClusterNodeNotFoundException(String message) {
            super(message, ErrorCode.E404_NOT_FOUND_CLUSTER_NODE);
        }

    }

}
