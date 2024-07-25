package kr.hakdang.cassdio.core.domain.cluster.keyspace.table;

import kr.hakdang.cassdio.core.domain.cluster.keyspace.CassandraSystemKeyspace;
import lombok.Getter;

import static kr.hakdang.cassdio.core.domain.cluster.keyspace.CassandraSystemKeyspace.SYSTEM;
import static kr.hakdang.cassdio.core.domain.cluster.keyspace.CassandraSystemKeyspace.SYSTEM_SCHEMA;

/**
 * CassandraSystemTable
 *
 * @author seungh0
 * @since 2024-07-02
 */
@Getter
public enum CassandraSystemTable {

    SYSTEM_SCHEMA_KEYSPACES(SYSTEM_SCHEMA, "keyspaces"),
    SYSTEM_SCHEMA_TABLES(SYSTEM_SCHEMA, "tables"),
    SYSTEM_SCHEMA_COLUMNS(SYSTEM_SCHEMA, "columns"),
    SYSTEM_SCHEMA_TYPES(SYSTEM_SCHEMA, "types"),
    SYSTEM_COMPACTION_HISTORY(SYSTEM, "compaction_history"),
    ;

    private final CassandraSystemKeyspace keyspace;
    private final String tableName;

    CassandraSystemTable(CassandraSystemKeyspace keyspace, String tableName) {
        this.keyspace = keyspace;
        this.tableName = tableName;
    }

}
