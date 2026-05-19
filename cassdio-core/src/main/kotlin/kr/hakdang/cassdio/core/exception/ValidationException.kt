package kr.hakdang.cassdio.core.exception

import kr.hakdang.cassdio.core.error.ErrorCode

class ValidationException(
    message: String = ErrorCode.INVALID_REQUEST.defaultMessage,
    cause: Throwable? = null,
) : ApiException(ErrorCode.INVALID_REQUEST, message, cause)
