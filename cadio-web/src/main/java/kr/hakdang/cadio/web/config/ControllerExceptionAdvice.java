package kr.hakdang.cadio.web.config;

import kr.hakdang.cadio.common.BaseException;
import kr.hakdang.cadio.common.ErrorCode;
import kr.hakdang.cadio.web.common.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

import static kr.hakdang.cadio.common.ErrorCode.E400_INVALID_PARAMETER;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    private ApiResponse<Object> handleBadRequest(BindException exception) {
        List<String> reasons = exception.getBindingResult().getFieldErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .toList();
        log.warn(exception.getMessage(), exception);
        return ApiResponse.fail(E400_INVALID_PARAMETER, reasons);
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
