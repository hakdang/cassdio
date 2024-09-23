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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public CqlSessionSelectResults columnList(String clusterId, String keyspace, String table) {
        return columnList(clusterId, keyspace, table, emptyList());
    }

    public CqlSessionSelectResults columnList(String clusterId, String keyspace, String table, List<String> columnList) {
        CqlSession session = cqlSessionFactory.get(clusterId);

        Select select = getColumnTable(session, keyspace)
            .all()
            .whereColumn(CassandraSystemTablesColumn.TABLES_KEYSPACE_NAME.getColumnName()).isEqualTo(bindMarker())
            .whereColumn(CassandraSystemTablesColumn.TABLES_TABLE_NAME.getColumnName()).isEqualTo(bindMarker());

        List<String> arr = new ArrayList<>();
        arr.add(keyspace);
        arr.add(table);

        if (CollectionUtils.isNotEmpty(columnList)) {
            select = select.whereColumn("column_name").in(columnList.stream()
                .map(info -> bindMarker())
                .collect(Collectors.toSet()));

            arr.addAll(columnList);
        }

        SimpleStatement statement = select.build(arr.toArray())
            .setTimeout(Duration.ofSeconds(3));

        ResultSet resultSet = session.execute(statement);

        List<Map<String, Object>> rows = convertRows(session, resultSet)
            .stream()
            .peek(row -> row.put("sortValue", makeSortValue(row)))
            .sorted(Comparator.comparing(row -> String.valueOf(row.get("sortValue"))))
            .toList();

        return CqlSessionSelectResults.of(
            rows,
            CassdioColumnDefinition.makes(resultSet.getColumnDefinitions())
        );
    }

    public List<String> columnSortedList(String clusterId, String keyspace, String table) {
        CqlSessionSelectResults results = columnList(clusterId, keyspace, table);
        return results.getRows().stream().map(row -> String.valueOf(row.get("column_name"))).collect(Collectors.toList());
    }

    private String makeSortValue(Map<String, Object> row) {
        ColumnKind columnKind = ColumnKind.findByCode(String.valueOf(row.get("kind")));
        return String.format("%s-%s", columnKind.getOrder(), row.get("position"));
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
