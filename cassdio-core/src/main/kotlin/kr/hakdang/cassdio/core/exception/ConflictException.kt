package kr.hakdang.cassdio.core.exception

import kr.hakdang.cassdio.core.error.ErrorCode

class ConflictException(
    message: String = ErrorCode.CONFLICT.defaultMessage,
    cause: Throwable? = null,
) : ApiException(ErrorCode.CONFLICT, message, cause)
