package kr.hakdang.cadio.core.domain.system.schema.table;

import lombok.Getter;

/**
 * SystemSchemaTablesColumn
 *
 * @author seungh0
 * @since 2024-07-02
 */
@Getter
public enum SystemSchemaTablesColumn {

    KEYSPACE_NAME("keyspace_name"),
    TABLE_NAME("table_name"),
    ;

    private final String columnName;

    SystemSchemaTablesColumn(String columnName) {
        this.columnName = columnName;
    }

}
