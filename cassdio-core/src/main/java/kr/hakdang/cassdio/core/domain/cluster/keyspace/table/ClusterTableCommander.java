package kr.hakdang.cassdio.core.domain.cluster.keyspace.table;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ColumnDefinition;
import com.datastax.oss.driver.api.core.cql.ColumnDefinitions;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.type.codec.registry.CodecRegistry;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.protocol.internal.util.Bytes;
import kr.hakdang.cassdio.core.domain.cluster.BaseClusterCommander;
import kr.hakdang.cassdio.core.domain.cluster.ClusterUtils;
import kr.hakdang.cassdio.core.domain.cluster.CqlSessionSelectResult;
import kr.hakdang.cassdio.core.domain.cluster.CqlSessionSelectResults;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.CassandraSystemKeyspace;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.CassdioColumnDefinition;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.ClusterTableArgs.ClusterTablePureSelectArgs;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.column.CassandraSystemTablesColumn;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

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
    public ClusterDescTablesResult allTables(CqlSession session, ClusterDescTablesArgs args) {
        SimpleStatement statement;
        int limit = 200; //cursor 적용 후 줄일 예정
        if (ClusterUtils.isVirtualKeyspace(session.getContext(), args.getKeyspace())) { //System
            statement = QueryBuilder
                .selectFrom(CassandraSystemKeyspace.SYSTEM_VIRTUAL_SCHEMA.getKeyspaceName(), CassandraSystemTable.SYSTEM_SCHEMA_TABLES.getTableName())
                .all()
                .whereColumn(CassandraSystemTablesColumn.TABLES_KEYSPACE_NAME.getColumnName()).isEqualTo(bindMarker())
                .limit(limit)
                .build()
                .setPageSize(limit)
                .setTimeout(Duration.ofSeconds(3));
        } else {
            statement = QueryBuilder
                .selectFrom(CassandraSystemKeyspace.SYSTEM_SCHEMA.getKeyspaceName(), CassandraSystemTable.SYSTEM_SCHEMA_TABLES.getTableName())
                .all()
                .whereColumn(CassandraSystemTablesColumn.TABLES_KEYSPACE_NAME.getColumnName()).isEqualTo(bindMarker())
                .limit(limit)
                .build()
                .setPageSize(limit)
                .setTimeout(Duration.ofSeconds(3));
        }

        PreparedStatement preparedStatement = session.prepare(statement);

        ResultSet resultSet = session.execute(preparedStatement.bind(args.getKeyspace()));
        Iterator<Row> page1Iter = resultSet.iterator();
        ColumnDefinitions definitions = resultSet.getColumnDefinitions();
        CodecRegistry codecRegistry = session.getContext().getCodecRegistry();

        List<Map<String, Object>> rows = new ArrayList<>();
        while (0 < resultSet.getAvailableWithoutFetching()) {
            rows.add(ClusterUtils.convertMap(codecRegistry, definitions, page1Iter.next()));
        }

        String nextCursor = "";
        ByteBuffer pagingState = resultSet.getExecutionInfo().getPagingState();
        if (pagingState != null) {
            nextCursor = Bytes.toHexString(pagingState);
        }

        List<String> columnNames = new ArrayList<>();
        for (ColumnDefinition definition : definitions) {
            columnNames.add(definition.getName().asCql(true));
        }

        return ClusterDescTablesResult.builder()
            .wasApplied(resultSet.wasApplied())
            .columnNames(columnNames)
            .rows(rows)
            .nextCursor(nextCursor)
            .build();
    }

    public CqlSessionSelectResults pureSelect(CqlSession session, ClusterTablePureSelectArgs args) {
        SimpleStatement statement = QueryBuilder.selectFrom(args.getKeyspace(), args.getTable())
            .all()
            .build()
            .setPageSize(args.getPageSize())
            .setTimeout(Duration.ofSeconds(3))  // 3s timeout
            .setPagingState(StringUtils.isNotBlank(args.getCursor()) ? Bytes.fromHexString(args.getCursor()) : null);

        ResultSet resultSet = session.execute(statement);
        Iterator<Row> page1Iter = resultSet.iterator();
        ColumnDefinitions definitions = resultSet.getColumnDefinitions();

        List<Map<String, Object>> rows = new ArrayList<>();
        CodecRegistry codecRegistry = session.getContext().getCodecRegistry();

        while (0 < resultSet.getAvailableWithoutFetching()) {
            rows.add(ClusterUtils.convertMap(codecRegistry, definitions, page1Iter.next()));
        }

        String nextCursor = "";
        ByteBuffer pagingState = resultSet.getExecutionInfo().getPagingState();
        if (pagingState != null) {
            nextCursor = Bytes.toHexString(pagingState);
        }

        return CqlSessionSelectResults.builder()
            .columnHeader(CassdioColumnDefinition.makes(definitions))
            .rows(rows)
            .nextCursor(nextCursor)
            .build();
    }

    public CqlSessionSelectResult tableDetail(CqlSession session, ClusterTableArgs.ClusterTableGetArgs args) {
        SimpleStatement statement;
        int limit = 1;
        if (ClusterUtils.isVirtualKeyspace(session.getContext(), args.getKeyspace())) { //System

            statement = QueryBuilder
                .selectFrom(CassandraSystemKeyspace.SYSTEM_VIRTUAL_SCHEMA.getKeyspaceName(), CassandraSystemTable.SYSTEM_SCHEMA_TABLES.getTableName())
                .all()
                .whereColumn(CassandraSystemTablesColumn.TABLES_KEYSPACE_NAME.getColumnName()).isEqualTo(bindMarker())
                .whereColumn(CassandraSystemTablesColumn.TABLES_TABLE_NAME.getColumnName()).isEqualTo(bindMarker())
                .limit(limit)
                .build(args.getKeyspace(), args.getTable())
                .setPageSize(limit)
                .setTimeout(Duration.ofSeconds(3));

        } else {
            statement = QueryBuilder
                .selectFrom(CassandraSystemKeyspace.SYSTEM_SCHEMA.getKeyspaceName(), CassandraSystemTable.SYSTEM_SCHEMA_TABLES.getTableName())
                .all()
                .whereColumn(CassandraSystemTablesColumn.TABLES_KEYSPACE_NAME.getColumnName()).isEqualTo(bindMarker())
                .whereColumn(CassandraSystemTablesColumn.TABLES_TABLE_NAME.getColumnName()).isEqualTo(bindMarker())
                .limit(limit)
                .build(args.getKeyspace(), args.getTable())
                .setPageSize(limit)
                .setTimeout(Duration.ofSeconds(3));
        }

        ResultSet resultSet = session.execute(statement);
        CodecRegistry codecRegistry = session.getContext().getCodecRegistry();
        ColumnDefinitions definitions = resultSet.getColumnDefinitions();
        Row row = resultSet.one();
        if (row == null) {
            throw new IllegalArgumentException(String.format("not found table(%s) in keyspace(%s)", args.getTable(), args.getKeyspace()));
        }

        return CqlSessionSelectResult.builder()
            .row(ClusterUtils.convertMap(codecRegistry, definitions, row))
            .columns(CassdioColumnDefinition.makes(definitions))
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
                .orElseThrow() //TODO : 에러처리
                .getTable(table)
                .orElseThrow()
                .describe(true);

        } catch (NoSuchElementException e) { //ignore
            return "";
        } catch (Throwable t) {
            throw t;
        }
    }
}
