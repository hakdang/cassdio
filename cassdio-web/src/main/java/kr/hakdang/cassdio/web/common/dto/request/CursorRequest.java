package kr.hakdang.cassdio.web.common.dto.request;

import jakarta.validation.constraints.Max;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * CursorRequest
 *
 * @author seungh0
 * @since 2024-07-01
 */
@ToString
@EqualsAndHashCode
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CursorRequest {

    private String cursor = null;

    @Max(value = 100, message = "size is less than 100")
    private int size = 50;

    @Builder
    private CursorRequest(String cursor, int size) {
        this.cursor = cursor;
        this.size = size;
    }

}
