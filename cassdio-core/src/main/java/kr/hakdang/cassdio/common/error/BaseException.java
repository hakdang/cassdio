package kr.hakdang.cassdio.common.error;

import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
public abstract class BaseException extends RuntimeException {

    private final ErrorCode errorCode;
    private final List<String> reasons;

    protected BaseException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.reasons = Collections.emptyList();
    }

    protected BaseException(String message, ErrorCode errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.reasons = Collections.emptyList();
    }

    protected BaseException(String message, ErrorCode errorCode, List<String> reasons) {
        super(message);
        this.errorCode = errorCode;
        this.reasons = reasons;
    }

    protected BaseException(String message, ErrorCode errorCode, Throwable cause, List<String> reasons) {
        super(message, cause);
        this.errorCode = errorCode;
        this.reasons = reasons;
    }

}
