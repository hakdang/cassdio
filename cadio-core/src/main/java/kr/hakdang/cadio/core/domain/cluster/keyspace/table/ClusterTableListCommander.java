package kr.hakdang.cadio.core.domain.cluster.keyspace.table;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.protocol.internal.util.Bytes;
import io.micrometer.common.util.StringUtils;
import kr.hakdang.cadio.core.domain.cluster.BaseClusterCommander;
import kr.hakdang.cadio.core.domain.cluster.keyspace.table.ClusterTableArgs.ClusterTableListArgs;
import kr.hakdang.cadio.core.domain.system.SystemKeyspace;
import kr.hakdang.cadio.core.domain.system.schema.SystemSchemaTable;
import kr.hakdang.cadio.core.domain.system.schema.table.SystemSchemaTablesColumn;
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
@Service
public class ClusterTableListCommander extends BaseClusterCommander {

    public ClusterTableListResult listTables(CqlSession session, ClusterTableListArgs args) {
        SimpleStatement statement = QueryBuilder
            .selectFrom(SystemKeyspace.SYSTEM_SCHEMA.getKeyspaceName(), SystemSchemaTable.TABLES.getTableName())
            .all()
            .whereColumn(SystemSchemaTablesColumn.KEYSPACE_NAME.getColumnName()).isEqualTo(bindMarker())
            .limit(args.getLimit())
            .build(args.getKeyspace())
            .setPagingState(StringUtils.isBlank(args.getNextPageState()) ? null : Bytes.fromHexString(args.getNextPageState()));

        ResultSet rs = session.execute(statement);

        List<ClusterTable> tables = rs.all().stream()
            .map(ClusterTable::from)
            .collect(Collectors.toList());

        return ClusterTableListResult.of(tables, rs.getExecutionInfo().getPagingState());
    }

}
