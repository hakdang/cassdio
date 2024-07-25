package kr.hakdang.cassdio.core.domain.cluster.keyspace.table;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.protocol.internal.util.Bytes;
import kr.hakdang.cassdio.core.domain.cluster.BaseClusterCommander;
import kr.hakdang.cassdio.core.domain.cluster.ClusterUtils;
import kr.hakdang.cassdio.core.domain.cluster.CqlSessionSelectResults;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.CassdioColumnDefinition;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.column.CassandraSystemTablesColumn;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;

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

    /**
     * Simple Table List
     * - system 테이블에 대해서도 테이블명에 대해 조회 가능
     *
     * @param session
     * @param args
     * @return
     */
    public CqlSessionSelectResults tableList(CqlSession session, TableDTO.ClusterTableListArgs args) {
        SimpleStatement statement = ClusterUtils.getSchemaTables(session, args.getKeyspace())
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

}
