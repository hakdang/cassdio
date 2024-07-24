package kr.hakdang.cassdio.core.domain.cluster.keyspace.table;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ColumnDefinitions;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.type.codec.registry.CodecRegistry;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import com.datastax.oss.driver.api.querybuilder.select.SelectFrom;
import com.datastax.oss.protocol.internal.util.Bytes;
import kr.hakdang.cassdio.core.domain.cluster.BaseClusterCommander;
import kr.hakdang.cassdio.core.domain.cluster.ClusterUtils;
import kr.hakdang.cassdio.core.domain.cluster.CqlSessionSelectResult;
import kr.hakdang.cassdio.core.domain.cluster.CqlSessionSelectResults;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.CassandraSystemKeyspace;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.CassdioColumnDefinition;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.ClusterKeyspaceException;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.ClusterTableArgs.ClusterTablePureSelectArgs;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.column.CassandraSystemTablesColumn;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.StreamSupport;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.bindMarker;

/**
 * ClusterTableCommander
 *
 * @author akageun
 * @since 2024-06-30
 */
@Slf4j
@Service
public class ClusterTableCommander extends BaseClusterCommander {

    /**
     * Simple Table List
     * - system 테이블에 대해서도 테이블명에 대해 조회 가능
     *
     * @param session
     * @param args
     * @return
     */
    public CqlSessionSelectResults allTables(CqlSession session, ClusterTableListArgs args) {
        SimpleStatement statement = getTable(session, args.getKeyspace())
            .all()
            .whereColumn(CassandraSystemTablesColumn.TABLES_KEYSPACE_NAME.getColumnName()).isEqualTo(bindMarker())
            .build()
            .setPageSize(args.getPageSize())
            .setTimeout(Duration.ofSeconds(3))
            .setPagingState(StringUtils.isNotBlank(args.getCursor()) ? Bytes.fromHexString(args.getCursor()) : null);

        PreparedStatement preparedStatement = session.prepare(statement);

        ResultSet resultSet = session.execute(preparedStatement.bind(args.getKeyspace()));

        return CqlSessionSelectResults.of(
            convertRows(session, resultSet),
            CassdioColumnDefinition.makes(resultSet.getColumnDefinitions()),
            resultSet.getExecutionInfo().getPagingState()
        );
    }

    public CqlSessionSelectResults pureSelect(CqlSession session, ClusterTablePureSelectArgs args) {
        SimpleStatement statement = QueryBuilder.selectFrom(args.getKeyspace(), args.getTable())
            .all()
            .build()
            .setPageSize(args.getPageSize())
            .setTimeout(Duration.ofSeconds(3))  // 3s timeout
            .setPagingState(StringUtils.isNotBlank(args.getCursor()) ? Bytes.fromHexString(args.getCursor()) : null);

        ResultSet resultSet = session.execute(statement);

        return CqlSessionSelectResults.of(
            convertRows(session, resultSet),
            CassdioColumnDefinition.makes(resultSet.getColumnDefinitions()),
            resultSet.getExecutionInfo().getPagingState()
        );
    }

    public CqlSessionSelectResult tableDetail(CqlSession session, ClusterTableArgs.ClusterTableGetArgs args) {
        int limit = 1;
        SimpleStatement statement = getTable(session, args.getKeyspace())
            .all()
            .whereColumn(CassandraSystemTablesColumn.TABLES_KEYSPACE_NAME.getColumnName()).isEqualTo(bindMarker())
            .whereColumn(CassandraSystemTablesColumn.TABLES_TABLE_NAME.getColumnName()).isEqualTo(bindMarker())
            .limit(limit)
            .build(args.getKeyspace(), args.getTable())
            .setPageSize(limit)
            .setTimeout(Duration.ofSeconds(3));

        ResultSet resultSet = session.execute(statement);
        ColumnDefinitions definitions = resultSet.getColumnDefinitions();
        Row row = resultSet.one();
        if (row == null) {
            throw new ClusterTableException.ClusterTableNotFoundException(String.format("not found table(%s)", args.getTable()));
        }

        return CqlSessionSelectResult.builder()
            .row(convertRow(session.getContext().getCodecRegistry(), definitions, row))
            .rowHeader(CassdioColumnDefinition.makes(definitions))
            .build();
    }

    public String tableDescribe(CqlSession session, String keyspace, String table) {
        if (ClusterUtils.isSystemKeyspace(session.getContext(), keyspace)) {
            return "";
        }

        //DESC 를 통한 정보 조회는 Cassandra 3.x 에서는 지원 안됨
        try {
            return session.getMetadata()
                .getKeyspace(keyspace)
                .orElseThrow(() -> new ClusterKeyspaceException.ClusterKeyspaceNotFoundException(String.format("not found keyspace (%s)", keyspace)))
                .getTable(table)
                .orElseThrow(() -> new ClusterTableException.ClusterTableNotFoundException(String.format("not found table(%s)", table)))
                .describe(true);

        } catch (NoSuchElementException e) { //ignore
            return "";
        }
    }

    public void tableDrop(CqlSession session, String keyspace, String table) {
        ResultSet resultSet = session.execute(SchemaBuilder.dropTable(keyspace, table).build());
        log.info("Table Drop Result - keyspace: {}, table: {}, ok: {}", keyspace, table, resultSet.wasApplied());
    }

    public void tableTruncate(CqlSession session, String keyspace, String table) {
        ResultSet resultSet = session.execute(QueryBuilder.truncate(keyspace, table).build());
        log.info("Table Truncate Result - keyspace: {}, table: {}, ok: {}", keyspace, table, resultSet.wasApplied());
    }

    private SelectFrom getTable(CqlSession session, String keyspace) {
        if (ClusterUtils.isVirtualKeyspace(session.getContext(), keyspace)) {
            return QueryBuilder
                .selectFrom(CassandraSystemKeyspace.SYSTEM_VIRTUAL_SCHEMA.getKeyspaceName(), CassandraSystemTable.SYSTEM_SCHEMA_TABLES.getTableName());
        }

        return QueryBuilder
            .selectFrom(CassandraSystemKeyspace.SYSTEM_SCHEMA.getKeyspaceName(), CassandraSystemTable.SYSTEM_SCHEMA_TABLES.getTableName());
    }
}
