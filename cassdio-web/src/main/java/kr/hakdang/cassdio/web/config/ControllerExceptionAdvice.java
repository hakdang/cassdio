package kr.hakdang.cassdio.web.config;

import kr.hakdang.cassdio.common.error.BaseException;
import kr.hakdang.cassdio.common.error.ErrorCode;
import kr.hakdang.cassdio.web.common.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static kr.hakdang.cassdio.common.error.ErrorCode.E400_INVALID_PARAMETER;

/**
 * ControllerExceptionAdvice
 *
 * @author seungh0
 * @since 2024-07-01
 */
@Slf4j
@RestControllerAdvice
public class ControllerExceptionAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    private ApiResponse<Object> handleBadRequest(BindException exception) {
        log.warn(exception.getMessage(), exception);
        return ApiResponse.fail(E400_INVALID_PARAMETER);
    }

    @ExceptionHandler(BaseException.class)
    private ResponseEntity<ApiResponse<Object>> handleBaseException(BaseException exception) {
        log.error(exception.getMessage(), exception);
        return ResponseEntity.status(exception.getErrorCode().getHttpStatusCode())
            .body(ApiResponse.fail(exception.getErrorCode()));
    }

    @ExceptionHandler(Throwable.class)
    private ResponseEntity<ApiResponse<Object>> handleThrowable(Throwable throwable) {
        log.error(throwable.getMessage(), throwable);
        return ResponseEntity.internalServerError()
            .body(ApiResponse.fail(ErrorCode.E500_INTERNAL_SERVER_ERROR));
    }

}
