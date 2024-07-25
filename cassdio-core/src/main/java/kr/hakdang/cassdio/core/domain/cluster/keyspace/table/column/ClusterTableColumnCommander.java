package kr.hakdang.cassdio.core.domain.cluster.keyspace.table.column;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import com.datastax.oss.driver.api.querybuilder.select.SelectFrom;
import kr.hakdang.cassdio.core.domain.cluster.BaseClusterCommander;
import kr.hakdang.cassdio.core.domain.cluster.ClusterUtils;
import kr.hakdang.cassdio.core.domain.cluster.CqlSessionSelectResults;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.CassandraSystemKeyspace;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.CassdioColumnDefinition;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.CassandraSystemTable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.bindMarker;
import static java.util.Collections.emptyList;

/**
 * ClusterTableColumnCommander
 *
 * @author akageun
 * @since 2024-07-06
 */
@Slf4j
@Service
public class ClusterTableColumnCommander extends BaseClusterCommander {

    public CqlSessionSelectResults columnList(CqlSession session, String keyspace, String table) {
        return columnList(session, keyspace, table, emptyList());
    }

    public CqlSessionSelectResults columnList(CqlSession session, String keyspace, String table, List<String> columnList) {
        SimpleStatement statement;

        Select select = getColumnTable(session, keyspace)
            .all()
            .whereColumn(CassandraSystemTablesColumn.TABLES_KEYSPACE_NAME.getColumnName()).isEqualTo(bindMarker())
            .whereColumn(CassandraSystemTablesColumn.TABLES_TABLE_NAME.getColumnName()).isEqualTo(bindMarker());

//        if (CollectionUtils.isNotEmpty(columnList)) {
//            select.whereColumn("column_name").in(columnList.stream().map(info -> bindMarker()).toList());
//        }

        statement = select.build(keyspace, table)
            .setTimeout(Duration.ofSeconds(3));

        ResultSet resultSet = session.execute(statement);

        return CqlSessionSelectResults.of(
            convertRows(session, resultSet),
            CassdioColumnDefinition.makes(resultSet.getColumnDefinitions())
        );
    }

    private SelectFrom getColumnTable(CqlSession session, String keyspace) {
        if (ClusterUtils.isVirtualKeyspace(session.getContext(), keyspace)) {
            return QueryBuilder
                .selectFrom(CassandraSystemKeyspace.SYSTEM_VIRTUAL_SCHEMA.getKeyspaceName(), CassandraSystemTable.SYSTEM_SCHEMA_COLUMNS.getTableName());
        }

        return QueryBuilder
            .selectFrom(CassandraSystemKeyspace.SYSTEM_SCHEMA.getKeyspaceName(), CassandraSystemTable.SYSTEM_SCHEMA_COLUMNS.getTableName());
    }
}
