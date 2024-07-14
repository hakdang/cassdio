package kr.hakdang.cassdio.core.domain.cluster.keyspace.table.column;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ColumnDefinition;
import com.datastax.oss.driver.api.core.cql.ColumnDefinitions;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.type.codec.registry.CodecRegistry;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.select.SelectFrom;
import com.datastax.oss.protocol.internal.util.Bytes;
import kr.hakdang.cassdio.core.domain.cluster.ClusterUtils;
import kr.hakdang.cassdio.core.domain.cluster.CqlSessionSelectResults;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.CassandraSystemKeyspace;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.CassdioColumnDefinition;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.CassandraSystemTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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

    public CqlSessionSelectResults columnList(CqlSession session, String keyspace, String table) {
        SimpleStatement statement;

        statement = getTable(session, keyspace).all()
            .whereColumn(CassandraSystemTablesColumn.TABLES_KEYSPACE_NAME.getColumnName()).isEqualTo(bindMarker())
            .whereColumn(CassandraSystemTablesColumn.TABLES_TABLE_NAME.getColumnName()).isEqualTo(bindMarker())
            .build(keyspace, table)
            .setTimeout(Duration.ofSeconds(3));

        ResultSet resultSet = session.execute(statement);
        Iterator<Row> page1Iter = resultSet.iterator();
        CodecRegistry codecRegistry = session.getContext().getCodecRegistry();
        ColumnDefinitions definitions = resultSet.getColumnDefinitions();

        List<Map<String, Object>> rows = new ArrayList<>();
        while (0 < resultSet.getAvailableWithoutFetching()) {
            rows.add(ClusterUtils.convertMap(codecRegistry, definitions, page1Iter.next()));
        }

        return CqlSessionSelectResults.builder()
            .rows(rows) //TODO 리스트 정렬해서 이쁘게 보여주기
            .columns(CassdioColumnDefinition.makes(definitions))
            .build();
    }

    private SelectFrom getTable(CqlSession session, String keyspace) {
        if (ClusterUtils.isVirtualKeyspace(session.getContext(), keyspace)) {
            return QueryBuilder
                .selectFrom(CassandraSystemKeyspace.SYSTEM_VIRTUAL_SCHEMA.getKeyspaceName(), CassandraSystemTable.SYSTEM_SCHEMA_COLUMNS.getTableName());
        }

        return QueryBuilder
            .selectFrom(CassandraSystemKeyspace.SYSTEM_SCHEMA.getKeyspaceName(), CassandraSystemTable.SYSTEM_SCHEMA_COLUMNS.getTableName());
    }
}
