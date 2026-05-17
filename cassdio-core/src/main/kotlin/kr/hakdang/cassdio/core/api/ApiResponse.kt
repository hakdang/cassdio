package kr.hakdang.cassdio.core.api

import com.fasterxml.jackson.annotation.JsonInclude
import kr.hakdang.cassdio.core.error.ErrorCode
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse<T>(
    val code: String,
    val message: String,
    val data: T? = null,
    val errors: List<FieldErrorResponse>? = null,
    val traceId: String? = null,
    val timestamp: Long = Instant.now().epochSecond,
) {
    companion object {
        fun <T> success(data: T): ApiResponse<T> =
            ApiResponse(
                code = "200",
                message = "Success",
                data = data,
            )

        fun success(): ApiResponse<Unit> =
            ApiResponse(
                code = "200",
                message = "Success",
            )

        fun failure(
            errorCode: ErrorCode,
            message: String = errorCode.defaultMessage,
            errors: List<FieldErrorResponse>? = null,
            traceId: String? = null,
        ): ApiResponse<Unit> =
            ApiResponse(
                code = errorCode.code,
                message = message,
                errors = errors,
                traceId = traceId,
            )
    }
}
