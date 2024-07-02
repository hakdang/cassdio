package kr.hakdang.cadio.core.domain.cluster.keyspace.table;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import kr.hakdang.cadio.IntegrationTest;
import kr.hakdang.cadio.core.domain.system.SystemKeyspace;
import kr.hakdang.cadio.core.domain.system.schema.SystemSchemaTable;
import kr.hakdang.cadio.core.domain.system.schema.table.SystemSchemaTablesColumn;
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
            .selectFrom(SystemKeyspace.SYSTEM_SCHEMA.getKeyspaceName(), SystemSchemaTable.TABLES.getTableName())
            .all()
            .whereColumn(SystemSchemaTablesColumn.KEYSPACE_NAME.getColumnName()).isEqualTo(bindMarker())
            .whereColumn(SystemSchemaTablesColumn.TABLE_NAME.getColumnName()).isEqualTo(bindMarker())
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
