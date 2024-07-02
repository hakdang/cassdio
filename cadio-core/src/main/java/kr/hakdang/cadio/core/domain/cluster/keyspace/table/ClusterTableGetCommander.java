package kr.hakdang.cadio.core.domain.cluster.keyspace.table;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import kr.hakdang.cadio.core.domain.cluster.BaseClusterCommander;
import kr.hakdang.cadio.core.domain.cluster.keyspace.table.ClusterTableArgs.ClusterTableGetArgs;
import kr.hakdang.cadio.core.domain.cluster.keyspace.table.column.Column;
import kr.hakdang.cadio.core.domain.system.SystemKeyspace;
import kr.hakdang.cadio.core.domain.system.schema.SystemSchemaTable;
import kr.hakdang.cadio.core.domain.system.schema.table.SystemSchemaTablesColumn;
import org.springframework.stereotype.Service;

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
        SimpleStatement statement = QueryBuilder
            .selectFrom(SystemKeyspace.SYSTEM_SCHEMA.getKeyspaceName(), SystemSchemaTable.TABLES.getTableName())
            .all()
            .whereColumn(SystemSchemaTablesColumn.KEYSPACE_NAME.getColumnName()).isEqualTo(bindMarker())
            .whereColumn(SystemSchemaTablesColumn.TABLE_NAME.getColumnName()).isEqualTo(bindMarker())
            .limit(1)
            .build(args.getKeyspace(), args.getTable());

        Row tableRow = session.execute(statement).one();
        if (tableRow == null) {
            throw new IllegalArgumentException(String.format("not found table(%s) in keyspace(%s)", args.getTable(), args.getKeyspace()));
        }

        return ClusterTableGetResult.builder()
            .table(ClusterTable.from(tableRow))
            .columns(getColumnsInTable(session, args))
            .build();
    }

    private List<Column> getColumnsInTable(CqlSession session, ClusterTableGetArgs args) {
        SimpleStatement statement = QueryBuilder.selectFrom(SystemKeyspace.SYSTEM_SCHEMA.getKeyspaceName(), SystemSchemaTable.COLUMNS.getTableName())
            .all()
            .whereColumn(SystemSchemaTablesColumn.KEYSPACE_NAME.getColumnName()).isEqualTo(bindMarker())
            .whereColumn(SystemSchemaTablesColumn.TABLE_NAME.getColumnName()).isEqualTo(bindMarker())
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
