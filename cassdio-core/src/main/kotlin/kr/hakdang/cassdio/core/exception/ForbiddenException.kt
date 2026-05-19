package kr.hakdang.cassdio.core.exception

import kr.hakdang.cassdio.core.error.ErrorCode

class ForbiddenException(
    message: String = ErrorCode.FORBIDDEN.defaultMessage,
    cause: Throwable? = null,
) : ApiException(ErrorCode.FORBIDDEN, message, cause)
