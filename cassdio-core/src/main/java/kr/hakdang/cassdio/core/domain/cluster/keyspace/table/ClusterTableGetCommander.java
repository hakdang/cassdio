package kr.hakdang.cassdio.core.domain.cluster.keyspace.table;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.metadata.schema.TableMetadata;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import kr.hakdang.cassdio.core.domain.cluster.BaseClusterCommander;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.CassandraSystemKeyspace;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.ClusterTableArgs.ClusterTableGetArgs;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.column.CassandraSystemTablesColumn;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.column.Column;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.bindMarker;

/**
 * ClusterTableGetCommander
 *
 * @author seungh0
 * @since 2024-07-02
 */
@Service
public class ClusterTableGetCommander extends BaseClusterCommander {

    public ClusterTableGetResult getTable(CqlSession session, ClusterTableGetArgs args) {
        int limit = 1;
        SimpleStatement statement = QueryBuilder
            .selectFrom(CassandraSystemKeyspace.SYSTEM_SCHEMA.getKeyspaceName(), CassandraSystemTable.SYSTEM_SCHEMA_TABLES.getTableName())
            .all()
            .whereColumn(CassandraSystemTablesColumn.TABLES_KEYSPACE_NAME.getColumnName()).isEqualTo(bindMarker())
            .whereColumn(CassandraSystemTablesColumn.TABLES_TABLE_NAME.getColumnName()).isEqualTo(bindMarker())
            .limit(limit)
            .build(args.getKeyspace(), args.getTable())
            .setPageSize(limit)
            .setTimeout(Duration.ofSeconds(3));

        Row tableRow = session.execute(statement).one();
        if (tableRow == null) {
            throw new IllegalArgumentException(String.format("not found table(%s) in keyspace(%s)", args.getTable(), args.getKeyspace()));
        }

        String tableDescribe = "";
        if (!CassandraSystemKeyspace.isSystemKeyspace(args.getKeyspace()) && args.isWithTableDescribe()) {
            TableMetadata tableMetadata = session.getMetadata().getKeyspace(args.getKeyspace())
                .orElseThrow(() -> new RuntimeException("not found keyspace"))
                .getTable(args.getTable())
                .orElseThrow(() -> new RuntimeException("not found table"));

            tableDescribe = tableMetadata.describe(true);
        }

        return ClusterTableGetResult.builder()
            .table(ClusterTable.from(tableRow))
            .tableDescribe(tableDescribe)
            .columns(getColumnsInTable(session, args))
            .build();
    }

    private List<Column> getColumnsInTable(CqlSession session, ClusterTableGetArgs args) {
        SimpleStatement statement = QueryBuilder.selectFrom(CassandraSystemKeyspace.SYSTEM_SCHEMA.getKeyspaceName(), CassandraSystemTable.SYSTEM_SCHEMA_COLUMNS.getTableName())
            .all()
            .whereColumn(CassandraSystemTablesColumn.TABLES_KEYSPACE_NAME.getColumnName()).isEqualTo(bindMarker())
            .whereColumn(CassandraSystemTablesColumn.TABLES_TABLE_NAME.getColumnName()).isEqualTo(bindMarker())
            .build(args.getKeyspace(), args.getTable());

        List<Row> columnRows = session.execute(statement).all();
        return columnRows.stream()
            .map(Column::from)
            .sorted(Comparator.comparing(o1 -> ((Column) o1).getKind().getOrder())
                .thenComparing(o1 -> ((Column) o1).getPosition()))
            .collect(Collectors.toList());
    }

}
