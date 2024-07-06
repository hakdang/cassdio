package kr.hakdang.cassdio.core.domain.cluster.keyspace.table;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import kr.hakdang.cassdio.IntegrationTest;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.CassandraSystemKeyspace;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.column.CassandraSystemTablesColumn;
import org.junit.jupiter.api.Test;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.bindMarker;

/**
 * ClusterTableOptionTest
 *
 * @author seungh0
 * @since 2024-07-02
 */
class ClusterTableOptionTest extends IntegrationTest {

    @Test
    void extra_table_options() {
        // given
        SimpleStatement statement = QueryBuilder
            .selectFrom(CassandraSystemKeyspace.SYSTEM_SCHEMA.getKeyspaceName(), CassandraSystemTable.SYSTEM_SCHEMA_TABLES.getTableName())
            .all()
            .whereColumn(CassandraSystemTablesColumn.TABLES_KEYSPACE_NAME.getColumnName()).isEqualTo(bindMarker())
            .whereColumn(CassandraSystemTablesColumn.TABLES_TABLE_NAME.getColumnName()).isEqualTo(bindMarker())
            .limit(1)
            .build(keyspaceName, "test_table_1");

        try (CqlSession session = makeSession()) {
            Row tableRow = session.execute(statement).one();

            // when & then
            for (ClusterTableOption option : ClusterTableOption.values()) {
                option.extract(tableRow);
            }
        }
    }

}
