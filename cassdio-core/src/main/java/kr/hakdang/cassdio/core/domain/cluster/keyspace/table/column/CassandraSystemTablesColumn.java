package kr.hakdang.cassdio.core.domain.cluster.keyspace.table.column;

import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.CassandraSystemTable;
import lombok.Getter;

/**
 * CassandraSystemTablesColumn
 *
 * @author seungh0
 * @since 2024-07-02
 */
@Getter
public enum CassandraSystemTablesColumn {

    TABLES_KEYSPACE_NAME(CassandraSystemTable.SYSTEM_SCHEMA_TABLES, "keyspace_name"),
    TABLES_TABLE_NAME(CassandraSystemTable.SYSTEM_SCHEMA_TABLES, "table_name"),
    TYPES_TYPE_NAME(CassandraSystemTable.SYSTEM_SCHEMA_TYPES, "type_name"),
    TYPES_FIELD_NAMES(CassandraSystemTable.SYSTEM_SCHEMA_TYPES, "field_names"),
    TYPES_FIELD_TYPES(CassandraSystemTable.SYSTEM_SCHEMA_TYPES, "field_types"),
    COMPACTION_HISTORY_KEYSPACE_NAME(CassandraSystemTable.SYSTEM_COMPACTION_HISTORY, "keyspace_name"),
    ;

    private final CassandraSystemTable table;
    private final String columnName;

    CassandraSystemTablesColumn(CassandraSystemTable table, String columnName) {
        this.table = table;
        this.columnName = columnName;
    }

}
