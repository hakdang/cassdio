package kr.hakdang.cadio.web.common.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * ItemListWithCursorResponse
 *
 * @author seungh0
 * @since 2024-07-01
 */
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemListWithCursorResponse<ITEM, CURSOR> {

    private List<ITEM> items;
    private CursorResponse<CURSOR> cursor;

    private ItemListWithCursorResponse(List<ITEM> items, CursorResponse<CURSOR> cursor) {
        this.items = items;
        this.cursor = cursor;
    }

    public static <ITEM, CURSOR> ItemListWithCursorResponse<ITEM, CURSOR> of(List<ITEM> items, CursorResponse<CURSOR> cursor) {
        return new ItemListWithCursorResponse<>(items, cursor);
    }

    public static <ITEM, CURSOR> ItemListWithCursorResponse<ITEM, CURSOR> noMore(List<ITEM> items) {
        return new ItemListWithCursorResponse<>(items, CursorResponse.noMore());
    }

}
