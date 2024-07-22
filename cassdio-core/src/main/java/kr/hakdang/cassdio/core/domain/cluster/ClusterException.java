package kr.hakdang.cassdio.core.domain.cluster;

import kr.hakdang.cassdio.common.error.BaseException;
import kr.hakdang.cassdio.common.error.ErrorCode;

/**
 * ClusterException
 *
 * @author seungh0
 * @since 2024-07-07
 */
public class ClusterException {

    public static class ClusterNodeNotFoundException extends BaseException {

        protected ClusterNodeNotFoundException(String message) {
            super(message, ErrorCode.E404_NOT_FOUND_CLUSTER_NODE);
        }

    }

}
