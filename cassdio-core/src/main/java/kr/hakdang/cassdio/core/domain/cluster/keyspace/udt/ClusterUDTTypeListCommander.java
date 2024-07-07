package kr.hakdang.cassdio.core.domain.cluster.keyspace.udt;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.protocol.internal.util.Bytes;
import io.micrometer.common.util.StringUtils;
import kr.hakdang.cassdio.core.domain.cluster.BaseClusterCommander;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.udt.ClusterUDTTypeArgs.ClusterUDTTypeListArgs;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.bindMarker;
import static kr.hakdang.cassdio.core.domain.cluster.keyspace.CassandraSystemKeyspace.SYSTEM_SCHEMA;
import static kr.hakdang.cassdio.core.domain.cluster.keyspace.table.CassandraSystemTable.SYSTEM_SCHEMA_TYPES;
import static kr.hakdang.cassdio.core.domain.cluster.keyspace.table.column.CassandraSystemTablesColumn.TABLES_KEYSPACE_NAME;

/**
 * ClusterUDTTypeListCommander
 *
 * @author seungh0
 * @since 2024-07-06
 */
@Service
public class ClusterUDTTypeListCommander extends BaseClusterCommander {

    public ClusterUDTTypeListResult listTypes(CqlSession session, ClusterUDTTypeListArgs args) {
        SimpleStatement statement = QueryBuilder
            .selectFrom(SYSTEM_SCHEMA.getKeyspaceName(), SYSTEM_SCHEMA_TYPES.getTableName())
            .all()
            .whereColumn(TABLES_KEYSPACE_NAME.getColumnName()).isEqualTo(bindMarker())
            .build(args.getKeyspace())
            .setPageSize(args.getPageSize())
            .setPagingState(StringUtils.isBlank(args.getNextPageState()) ? null : Bytes.fromHexString(args.getNextPageState()));

        ResultSet rs = session.execute(statement);

        List<ClusterUDTType> types = StreamSupport.stream(rs.spliterator(), false)
            .limit(rs.getAvailableWithoutFetching())
            .map(ClusterUDTType::from)
            .collect(Collectors.toList());

        return ClusterUDTTypeListResult.of(types, rs.getExecutionInfo().getPagingState());
    }

}
