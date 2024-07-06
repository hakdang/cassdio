package kr.hakdang.cassdio.web.common.dto.request;

import kr.hakdang.cassdio.common.model.Direction;
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
    private int size = 30;
    private Direction direction = Direction.NEXT;

    @Builder
    private CursorRequest(String cursor, int size, Direction direction) {
        this.cursor = cursor;
        this.size = size;
        this.direction = direction;
    }

}
