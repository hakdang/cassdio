package kr.hakdang.cadio.common;

import lombok.Getter;

@Getter
public enum ErrorCode {

    E400_INVALID_PARAMETER(400, "invalid_parameter"),
    E500_INTERNAL_SERVER_ERROR(500, "internal_server_error"),
    ;

    private final int httpStatusCode;
    private final String code;

    ErrorCode(int httpStatusCode, String code) {
        this.httpStatusCode = httpStatusCode;
        this.code = code;
    }

}
