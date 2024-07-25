package kr.hakdang.cassdio.core.domain.cluster.keyspace.table;

import kr.hakdang.cassdio.IntegrationTest;

/**
 * ClusterTableOptionTest
 *
 * @author seungh0
 * @since 2024-07-02
 */
class ClusterTableOptionTest extends IntegrationTest {

//    @Test
//    void extract_table_option() {
//        // given
//        SimpleStatement statement = QueryBuilder
//            .selectFrom(CassandraSystemKeyspace.SYSTEM_SCHEMA.getKeyspaceName(), CassandraSystemTable.SYSTEM_SCHEMA_TABLES.getTableName())
//            .all()
//            .whereColumn(CassandraSystemTablesColumn.TABLES_KEYSPACE_NAME.getColumnName()).isEqualTo(bindMarker())
//            .whereColumn(CassandraSystemTablesColumn.TABLES_TABLE_NAME.getColumnName()).isEqualTo(bindMarker())
//            .limit(1)
//            .build(keyspaceName, "test_table_1");
//
//        try (CqlSession session = makeSession()) {
//            Row tableRow = session.execute(statement).one();
//
//            // when & then
//            for (ClusterTableOption option : ClusterTableOption.values()) {
//                option.extract(tableRow);
//            }
//        }
//    }

}
