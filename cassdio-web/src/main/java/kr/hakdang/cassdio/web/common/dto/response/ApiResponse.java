package kr.hakdang.cassdio.web.common.dto.response;

import kr.hakdang.cassdio.common.error.ErrorCode;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

    private boolean ok;
    private String error;
    private T result;
    private String message;

    private ApiResponse(boolean ok, String error, T result, String message) {
        this.ok = ok;
        this.error = error;
        this.result = result;
        this.message = message;
    }

    public static <T> ApiResponse<T> ok(T result) {
        return new ApiResponse<>(true, null, result, null);
    }

    public static ApiResponse<Void> ok() {
        return new ApiResponse<>(true, null, null, null);
    }

    public static <T> ApiResponse<T> fail(ErrorCode errorCode) {
        return new ApiResponse<>(false, errorCode.getCode(), null, null);
    }
    public static <T> ApiResponse<T> fail(ErrorCode errorCode, String message) {
        return new ApiResponse<>(false, errorCode.getCode(), null, message);
    }

}
