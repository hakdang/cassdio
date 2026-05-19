package kr.hakdang.cassdio.core.error

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolationException
import kr.hakdang.cassdio.core.api.ApiResponse
import kr.hakdang.cassdio.core.api.FieldErrorResponse
import kr.hakdang.cassdio.core.exception.ApiException
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.slf4j.event.Level
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.util.UUID

@RestControllerAdvice
class GlobalExceptionHandler(
    private val errorMessageResolver: ErrorMessageResolver,
) {
    @ExceptionHandler(ApiException::class)
    fun handleApiException(
        exception: ApiException,
        request: HttpServletRequest,
    ): ResponseEntity<ApiResponse<Unit>> {
        val traceId = traceId()
        logException(exception.errorCode, exception, request, traceId)

        return ResponseEntity
            .status(exception.errorCode.status)
            .body(
                ApiResponse.failure(
                    errorCode = exception.errorCode,
                    message = exception.message ?: errorMessageResolver.resolve(exception.errorCode),
                    traceId = traceId,
                ),
            )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(
        exception: MethodArgumentNotValidException,
        request: HttpServletRequest,
    ): ResponseEntity<ApiResponse<Unit>> =
        validationFailure(
            exception = exception,
            request = request,
            errors = exception.bindingResult.fieldErrors.map { it.toResponse() },
        )

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(
        exception: ConstraintViolationException,
        request: HttpServletRequest,
    ): ResponseEntity<ApiResponse<Unit>> =
        validationFailure(
            exception = exception,
            request = request,
            errors =
                exception.constraintViolations.map {
                    FieldErrorResponse(
                        field = it.propertyPath.toString(),
                        message = it.message,
                        rejectedValue = it.invalidValue?.toString(),
                    )
                },
        )

    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(
        exception: RuntimeException,
        request: HttpServletRequest,
    ): ResponseEntity<ApiResponse<Unit>> = internalServerError(exception, request)

    @ExceptionHandler(Exception::class)
    fun handleException(
        exception: Exception,
        request: HttpServletRequest,
    ): ResponseEntity<ApiResponse<Unit>> = internalServerError(exception, request)

    private fun validationFailure(
        exception: Exception,
        request: HttpServletRequest,
        errors: List<FieldErrorResponse>,
    ): ResponseEntity<ApiResponse<Unit>> {
        val traceId = traceId()
        logException(ErrorCode.INVALID_REQUEST, exception, request, traceId)

        return ResponseEntity
            .status(ErrorCode.INVALID_REQUEST.status)
            .body(
                ApiResponse.failure(
                    errorCode = ErrorCode.INVALID_REQUEST,
                    message = errorMessageResolver.resolve(ErrorCode.INVALID_REQUEST),
                    errors = errors,
                    traceId = traceId,
                ),
            )
    }

    private fun internalServerError(
        exception: Exception,
        request: HttpServletRequest,
    ): ResponseEntity<ApiResponse<Unit>> {
        val traceId = traceId()
        logException(ErrorCode.INTERNAL_SERVER_ERROR, exception, request, traceId)

        return ResponseEntity
            .status(ErrorCode.INTERNAL_SERVER_ERROR.status)
            .body(
                ApiResponse.failure(
                    errorCode = ErrorCode.INTERNAL_SERVER_ERROR,
                    message = errorMessageResolver.resolve(ErrorCode.INTERNAL_SERVER_ERROR),
                    traceId = traceId,
                ),
            )
    }

    private fun FieldError.toResponse(): FieldErrorResponse =
        FieldErrorResponse(
            field = field,
            message = defaultMessage ?: "Invalid value",
            rejectedValue = rejectedValue?.toString(),
        )

    private fun traceId(): String = MDC.get(TRACE_ID_MDC_KEY) ?: UUID.randomUUID().toString()

    private fun logException(
        errorCode: ErrorCode,
        exception: Exception,
        request: HttpServletRequest,
        traceId: String,
    ) {
        val policy =
            if (errorCode.status.is5xxServerError) {
                ErrorLoggingPolicy.SERVER_ERROR
            } else {
                ErrorLoggingPolicy.CLIENT_ERROR
            }
        val message = "api_error traceId={} code={} method={} path={}"

        when (policy.level) {
            Level.ERROR -> log.error(message, traceId, errorCode.code, request.method, request.requestURI, exception)
            else -> log.warn(message, traceId, errorCode.code, request.method, request.requestURI)
        }
    }

    companion object {
        const val TRACE_ID_MDC_KEY = "traceId"

        private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    }
}
