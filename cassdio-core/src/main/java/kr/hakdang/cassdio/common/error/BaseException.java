package kr.hakdang.cassdio.common.error;

import lombok.Getter;

/**
 * BaseException
 *
 * @author seungh0
 * @since 2024-07-01
 */
@Getter
public abstract class BaseException extends RuntimeException {

    private final ErrorCode errorCode;

    protected BaseException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    protected BaseException(String message, ErrorCode errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

}
