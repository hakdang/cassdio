package kr.hakdang.cassdio.core.exception

import kr.hakdang.cassdio.core.error.ErrorCode

class UnauthorizedException(
    message: String = ErrorCode.UNAUTHORIZED.defaultMessage,
    cause: Throwable? = null,
) : ApiException(ErrorCode.UNAUTHORIZED, message, cause)
