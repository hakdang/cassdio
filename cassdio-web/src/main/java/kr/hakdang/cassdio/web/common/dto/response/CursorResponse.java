package kr.hakdang.cassdio.web.common.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * CursorResponse
 *
 * @author seungh0
 * @since 2024-07-01
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CursorResponse<T> {

    private T next;
    private boolean hasNext;

    @Builder
    private CursorResponse(T next, boolean hasNext) {
        this.next = next;
        this.hasNext = hasNext;
    }

    public static <T> CursorResponse<T> noMore() {
        return new CursorResponse<>(null, false);
    }

    public static <T> CursorResponse<T> withNext(T next) {
        return new CursorResponse<>(next, next != null);
    }

}
