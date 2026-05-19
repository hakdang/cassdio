package kr.hakdang.cassdio.core.exception

import kr.hakdang.cassdio.core.error.ErrorCode

class NotFoundException(
    message: String = ErrorCode.NOT_FOUND.defaultMessage,
    cause: Throwable? = null,
) : ApiException(ErrorCode.NOT_FOUND, message, cause)
