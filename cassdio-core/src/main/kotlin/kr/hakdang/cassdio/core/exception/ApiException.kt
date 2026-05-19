package kr.hakdang.cassdio.core.exception

import kr.hakdang.cassdio.core.error.ErrorCode

open class ApiException(
    val errorCode: ErrorCode,
    override val message: String = errorCode.defaultMessage,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
