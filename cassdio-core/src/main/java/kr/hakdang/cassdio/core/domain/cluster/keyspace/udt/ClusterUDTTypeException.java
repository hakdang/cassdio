package kr.hakdang.cassdio.core.domain.cluster.keyspace.udt;

import kr.hakdang.cassdio.common.error.BaseException;
import kr.hakdang.cassdio.common.error.ErrorCode;

/**
 * ClusterUDTTypeException
 *
 * @author Seungho Kang (will.seungho@webtoonscorp.com)
 * @version 1.0.0
 * @since 2024. 07. 07.
 */
public class ClusterUDTTypeException {

    public static class ClusterUDTTypeNotFoundException extends BaseException {

        protected ClusterUDTTypeNotFoundException(String message) {
            super(message, ErrorCode.E404_NOT_FOUND_UDT_TYPE);
        }

    }

}
