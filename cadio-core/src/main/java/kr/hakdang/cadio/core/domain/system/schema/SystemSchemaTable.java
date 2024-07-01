package kr.hakdang.cadio.core.domain.system.schema;

import lombok.Getter;

/**
 * SystemSchemaTable
 *
 * @author seungh0
 * @since 2024-07-02
 */
@Getter
public enum SystemSchemaTable {

    TABLES("tables"),
    COLUMNS("columns"),
    ;

    private final String tableName;

    SystemSchemaTable(String tableName) {
        this.tableName = tableName;
    }

}
