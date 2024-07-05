package kr.hakdang.cadio.core.domain.cluster.keyspace.table;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.protocol.internal.util.Bytes;
import io.micrometer.common.util.StringUtils;
import kr.hakdang.cadio.core.domain.cluster.BaseClusterCommander;
import kr.hakdang.cadio.core.domain.cluster.keyspace.table.ClusterTableArgs.ClusterTableListArgs;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.bindMarker;
import static kr.hakdang.cadio.core.domain.cluster.keyspace.CassandraSystemKeyspace.SYSTEM_SCHEMA;
import static kr.hakdang.cadio.core.domain.cluster.keyspace.table.CassandraSystemTable.SYSTEM_SCHEMA_TABLES;
import static kr.hakdang.cadio.core.domain.cluster.keyspace.table.column.CassandraSystemTablesColumn.TABLES_KEYSPACE_NAME;

/**
 * ClusterTableListCommander
 *
 * @author seungh0
 * @since 2024-07-01
 */
@Service
public class ClusterTableListCommander extends BaseClusterCommander {

    public ClusterTableListResult listTables(CqlSession session, ClusterTableListArgs args) {
        SimpleStatement statement = QueryBuilder
            .selectFrom(SYSTEM_SCHEMA.getKeyspaceName(), SYSTEM_SCHEMA_TABLES.getTableName())
            .all()
            .whereColumn(TABLES_KEYSPACE_NAME.getColumnName()).isEqualTo(bindMarker())
            .build(args.getKeyspace())
            .setPageSize(args.getPageSize())
            .setPagingState(StringUtils.isBlank(args.getNextPageState()) ? null : Bytes.fromHexString(args.getNextPageState()));

        ResultSet rs = session.execute(statement);

        List<ClusterTable> tables = StreamSupport.stream(rs.spliterator(), false)
            .limit(rs.getAvailableWithoutFetching())
            .map(ClusterTable::from)
            .collect(Collectors.toList());

        return ClusterTableListResult.of(tables, rs.getExecutionInfo().getPagingState());
    }

}
