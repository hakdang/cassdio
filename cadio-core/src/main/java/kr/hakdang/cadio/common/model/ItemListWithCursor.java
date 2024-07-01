package kr.hakdang.cadio.common.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * ItemListWithCursor
 *
 * @author seungh0
 * @since 2024-07-01
 */
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemListWithCursor<ITEM, CURSOR> {

    private List<ITEM> items;
    private Cursor<CURSOR> cursor;

    private ItemListWithCursor(List<ITEM> items, Cursor<CURSOR> cursor) {
        this.items = items;
        this.cursor = cursor;
    }

    public static <ITEM, CURSOR> ItemListWithCursor<ITEM, CURSOR> of(List<ITEM> items, Cursor<CURSOR> cursor) {
        return new ItemListWithCursor<>(items, cursor);
    }

}
