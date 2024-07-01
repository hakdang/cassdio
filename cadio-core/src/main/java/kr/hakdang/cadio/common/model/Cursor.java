package kr.hakdang.cadio.common.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Cursor
 *
 * @author seungh0
 * @since 2024-07-01
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Cursor<T> {

    private T next;

    @Builder
    private Cursor(T next) {
        this.next = next;
    }

    public static <T> Cursor<T> noMore() {
        return new Cursor<>(null);
    }

}
