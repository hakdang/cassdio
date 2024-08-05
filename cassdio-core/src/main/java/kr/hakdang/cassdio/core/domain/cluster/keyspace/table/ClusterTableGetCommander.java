package kr.hakdang.cassdio.core.domain.cluster.keyspace.table;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ColumnDefinitions;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import kr.hakdang.cassdio.core.domain.cluster.BaseClusterCommander;
import kr.hakdang.cassdio.core.domain.cluster.ClusterUtils;
import kr.hakdang.cassdio.core.domain.cluster.CqlSessionSelectResult;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.CassdioColumnDefinition;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.column.CassandraSystemTablesColumn;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.bindMarker;

/**
 * ClusterTableGetCommander
 *
 * @author seungh0
 * @since 2024-07-02
 */
@Service
public class ClusterTableGetCommander extends BaseClusterCommander {

    public CqlSessionSelectResult tableDetail(String clusterId, TableDTO.ClusterTableGetArgs args) {
        CqlSession session = cqlSessionFactory.get(clusterId);

        int limit = 1;
        SimpleStatement statement = ClusterUtils.getSchemaTables(session, args.getKeyspace())
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

}
