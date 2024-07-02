package kr.hakdang.cadio.core.domain.cluster.keyspace.table;

import kr.hakdang.cadio.core.domain.cluster.keyspace.CassandraSystemKeyspace;
import lombok.Getter;

import static kr.hakdang.cadio.core.domain.cluster.keyspace.CassandraSystemKeyspace.SYSTEM_SCHEMA;

/**
 * CassandraSystemTable
 *
 * @author seungh0
 * @since 2024-07-02
 */
@Getter
public enum CassandraSystemTable {

    SYSTEM_SCHEMA_TABLES(SYSTEM_SCHEMA, "tables"),
    SYSTEM_SCHEMA_COLUMNS(SYSTEM_SCHEMA, "columns"),
    ;

    private final CassandraSystemKeyspace keyspace;
    private final String tableName;

    CassandraSystemTable(CassandraSystemKeyspace keyspace, String tableName) {
        this.keyspace = keyspace;
        this.tableName = tableName;
    }

}
