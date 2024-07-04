package kr.hakdang.cadio.core.domain.cluster.keyspace.table;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.metadata.schema.KeyspaceMetadata;
import com.datastax.oss.driver.api.core.metadata.schema.TableMetadata;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import kr.hakdang.cadio.core.domain.cluster.BaseClusterCommander;
import kr.hakdang.cadio.core.domain.cluster.keyspace.CassandraSystemKeyspace;
import kr.hakdang.cadio.core.domain.cluster.keyspace.table.ClusterTableArgs.ClusterTableGetArgs;
import kr.hakdang.cadio.core.domain.cluster.keyspace.table.column.CassandraSystemTablesColumn;
import kr.hakdang.cadio.core.domain.cluster.keyspace.table.column.Column;
import org.springframework.stereotype.Service;

import java.time.Duration;
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
        if (args.isWithTableDescribe()) {
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
            .sorted((o1, o2) -> {
                if (o1.getKind().getOrder() < o2.getKind().getOrder()) {
                    return -1;
                } else if (o1.getKind().getOrder() > o2.getKind().getOrder()) {
                    return 1;
                }
                return o1.getPosition() - o2.getPosition();
            })
            .collect(Collectors.toList());
    }

}
