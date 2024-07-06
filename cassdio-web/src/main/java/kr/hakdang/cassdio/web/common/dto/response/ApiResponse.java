package kr.hakdang.cassdio.web.common.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.hakdang.cassdio.common.ErrorCode;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Collections;
import java.util.List;

@EqualsAndHashCode
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

    public static final ApiResponse<Object> OK = ok(null);

    private boolean ok;

    private String error;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> reasons;

    private T result;

    private ApiResponse(boolean ok, String error, List<String> reasons, T result) {
        this.ok = ok;
        this.error = error;
        this.reasons = reasons;
        this.result = result;
    }

    public static <T> ApiResponse<T> ok(T result) {
        return new ApiResponse<>(true, null, Collections.emptyList(), result);
    }

    public static <T> ApiResponse<T> fail(ErrorCode errorCode) {
        return new ApiResponse<>(false, errorCode.getCode(), Collections.emptyList(), null);
    }

    public static <T> ApiResponse<T> fail(ErrorCode errorCode, List<String> reasons) {
        return new ApiResponse<>(false, errorCode.getCode(), reasons, null);
    }

}
