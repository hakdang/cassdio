package kr.hakdang.cassdio.core.domain.cluster.keyspace.table;

import kr.hakdang.cassdio.common.error.BaseException;
import kr.hakdang.cassdio.common.error.ErrorCode;

/**
 * ClusterTableException
 *
 * @author Seungho Kang (will.seungho@webtoonscorp.com)
 * @version 1.0.0
 * @since 2024. 07. 07.
 */
public class ClusterTableException {

    public static class CLusterTableNotFoundException extends BaseException {

        protected CLusterTableNotFoundException(String message) {
            super(message, ErrorCode.E404_NOT_FOUND_CLUSTER_NODE);
        }
    }

}
