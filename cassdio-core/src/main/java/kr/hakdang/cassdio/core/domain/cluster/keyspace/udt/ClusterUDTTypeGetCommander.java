package kr.hakdang.cassdio.core.domain.cluster.keyspace.udt;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import kr.hakdang.cassdio.core.domain.cluster.BaseClusterCommander;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.udt.ClusterUDTTypeArgs.ClusterUDTTypeGetArgs;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.udt.ClusterUDTTypeException.ClusterUDTTypeNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.StreamSupport;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.bindMarker;
import static kr.hakdang.cassdio.core.domain.cluster.keyspace.CassandraSystemKeyspace.SYSTEM_SCHEMA;
import static kr.hakdang.cassdio.core.domain.cluster.keyspace.table.CassandraSystemTable.SYSTEM_SCHEMA_TYPES;
import static kr.hakdang.cassdio.core.domain.cluster.keyspace.table.column.CassandraSystemTablesColumn.TABLES_KEYSPACE_NAME;
import static kr.hakdang.cassdio.core.domain.cluster.keyspace.table.column.CassandraSystemTablesColumn.TYPES_TYPE_NAME;

/**
 * ClusterUDTTypeListCommander
 *
 * @author seungh0
 * @since 2024-07-06
 */
@Service
public class ClusterUDTTypeGetCommander extends BaseClusterCommander {

    public ClusterUDTType getType(CqlSession session, ClusterUDTTypeGetArgs args) {
        SimpleStatement statement = QueryBuilder
            .selectFrom(SYSTEM_SCHEMA.getKeyspaceName(), SYSTEM_SCHEMA_TYPES.getTableName())
            .all()
            .whereColumn(TABLES_KEYSPACE_NAME.getColumnName()).isEqualTo(bindMarker())
            .whereColumn(TYPES_TYPE_NAME.getColumnName()).isEqualTo(bindMarker())
            .build(args.getKeyspace(), args.getType())
            .setPageSize(1);

        ResultSet rs = session.execute(statement);

        return StreamSupport.stream(rs.spliterator(), false)
            .limit(1)
            .map(ClusterUDTType::from)
            .findFirst()
            .orElseThrow(() -> new ClusterUDTTypeNotFoundException(String.format("not exists udt type(%s) in keyspace(%s)", args.getType(), args.getKeyspace())));
    }

}
