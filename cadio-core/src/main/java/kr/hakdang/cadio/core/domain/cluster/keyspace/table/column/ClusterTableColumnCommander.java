package kr.hakdang.cadio.core.domain.cluster.keyspace.table.column;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ColumnDefinitions;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.type.codec.registry.CodecRegistry;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import kr.hakdang.cadio.core.domain.cluster.ClusterUtils;
import kr.hakdang.cadio.core.domain.cluster.keyspace.CassandraSystemKeyspace;
import kr.hakdang.cadio.core.domain.cluster.keyspace.table.CassandraSystemTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.bindMarker;

/**
 * ClusterTableColumnCommander
 *
 * @author akageun
 * @since 2024-07-06
 */
@Slf4j
@Service
public class ClusterTableColumnCommander {

    public void columnList(CqlSession session, String keyspace, String table) {
        SimpleStatement statement;
        int limit = 1000; //변경필요
        if (ClusterUtils.isVirtualKeyspace(session.getContext(), keyspace)) { //System

            statement = QueryBuilder
                .selectFrom(CassandraSystemKeyspace.SYSTEM_VIRTUAL_SCHEMA.getKeyspaceName(), CassandraSystemTable.SYSTEM_SCHEMA_TABLES.getTableName())
                .all()
                .whereColumn(CassandraSystemTablesColumn.TABLES_KEYSPACE_NAME.getColumnName()).isEqualTo(bindMarker())
                .whereColumn(CassandraSystemTablesColumn.TABLES_TABLE_NAME.getColumnName()).isEqualTo(bindMarker())
                .limit(limit)
                .build(keyspace, table)
                .setPageSize(limit)
                .setTimeout(Duration.ofSeconds(3));

        } else {
            statement = QueryBuilder
                .selectFrom(CassandraSystemKeyspace.SYSTEM_SCHEMA.getKeyspaceName(), CassandraSystemTable.SYSTEM_SCHEMA_TABLES.getTableName())
                .all()
                .whereColumn(CassandraSystemTablesColumn.TABLES_KEYSPACE_NAME.getColumnName()).isEqualTo(bindMarker())
                .whereColumn(CassandraSystemTablesColumn.TABLES_TABLE_NAME.getColumnName()).isEqualTo(bindMarker())
                .limit(limit)
                .build(keyspace, table)
                .setPageSize(limit)
                .setTimeout(Duration.ofSeconds(3));
        }

        ResultSet resultSet = session.execute(statement);
        CodecRegistry codecRegistry = session.getContext().getCodecRegistry();
        ColumnDefinitions definitions = resultSet.getColumnDefinitions();
        Row row = resultSet.one();
        if (row == null) {
            //throw new IllegalArgumentException(String.format("not found table(%s) in keyspace(%s)", args.getTable(), args.getKeyspace()));
        }

        ClusterUtils.convertMap(codecRegistry, definitions, row);
    }
}
