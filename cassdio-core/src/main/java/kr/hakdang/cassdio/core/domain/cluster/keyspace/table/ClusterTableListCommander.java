package kr.hakdang.cassdio.core.domain.cluster.keyspace.table;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.protocol.internal.util.Bytes;
import io.micrometer.common.util.StringUtils;
import kr.hakdang.cassdio.core.domain.cluster.BaseClusterCommander;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.ClusterTableArgs.ClusterTableListArgs;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.CassandraSystemKeyspace;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.column.CassandraSystemTablesColumn;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.bindMarker;

/**
 * ClusterTableListCommander
 *
 * @author seungh0
 * @since 2024-07-01
 */
@Slf4j
@Service
public class ClusterTableListCommander extends BaseClusterCommander {

    public ClusterTableListResult listTables(CqlSession session, ClusterTableListArgs args) {
        SimpleStatement statement = QueryBuilder
            .selectFrom(CassandraSystemKeyspace.SYSTEM_SCHEMA.getKeyspaceName(), CassandraSystemTable.SYSTEM_SCHEMA_TABLES.getTableName())
            .all()
            .whereColumn(CassandraSystemTablesColumn.TABLES_KEYSPACE_NAME.getColumnName()).isEqualTo(bindMarker())
            .limit(args.getLimit())
            .build(args.getKeyspace())
            .setPagingState(StringUtils.isBlank(args.getNextPageState()) ? null : Bytes.fromHexString(args.getNextPageState()));

//        for (Map.Entry<CqlIdentifier, TableMetadata> entry : session.getMetadata().getKeyspace(args.getKeyspace())
//            .orElseThrow().getTables().entrySet()) {
//            log.info("key : {}, value : {}", entry.getKey(), entry.getValue());
//        }

//        ResultSet rs2 = session.execute("DESC " + args.getKeyspace());
//        for (Row row : rs2.all()) {
//            log.info("222row : {}", row.getFormattedContents());
//        }

        ResultSet rs = session.execute(statement);

        List<ClusterTable> tables = rs.all().stream()
            .map(ClusterTable::from)
            .collect(Collectors.toList());

        return ClusterTableListResult.of(tables, rs.getExecutionInfo().getPagingState());
    }

}