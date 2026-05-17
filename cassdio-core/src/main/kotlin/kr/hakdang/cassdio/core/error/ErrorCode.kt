package kr.hakdang.cassdio.core.error

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val code: String,
    val defaultMessage: String,
    val status: HttpStatus,
    val messageKey: String = "error.$code",
) {
    INVALID_REQUEST("E001", "Invalid request", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("E002", "Unauthorized", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("E003", "Forbidden", HttpStatus.FORBIDDEN),
    NOT_FOUND("E004", "Resource not found", HttpStatus.NOT_FOUND),
    CONFLICT("E005", "Conflict", HttpStatus.CONFLICT),
    INTERNAL_SERVER_ERROR("E999", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
}
