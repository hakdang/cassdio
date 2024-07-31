package kr.hakdang.cassdio.core.domain.cluster.client;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.Version;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import kr.hakdang.cassdio.common.error.NotSupportedCassandraVersionException;
import kr.hakdang.cassdio.core.domain.cluster.BaseClusterCommander;
import kr.hakdang.cassdio.core.domain.cluster.ClusterVersionEvaluator;
import kr.hakdang.cassdio.core.domain.cluster.CqlSessionFactory;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.CassandraSystemKeyspace;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.CassandraSystemTable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

/**
 * ClusterClientListCommander
 *
 * @author seungh0
 * @since 2024-07-25
 */
@Service
public class ClusterClientListCommander extends BaseClusterCommander {

    private final ClusterVersionEvaluator clusterVersionEvaluator;

    public ClusterClientListCommander(
        ClusterVersionEvaluator clusterVersionEvaluator,
        CqlSessionFactory cqlSessionFactory
    ) {
        this.clusterVersionEvaluator = clusterVersionEvaluator;
        this.cqlSessionFactory = cqlSessionFactory;
    }

    public ClusterClientListResult getClients(String clusterId) {
        CqlSession session = cqlSessionFactory.get(clusterId);
        if (clusterVersionEvaluator.isLessThan(clusterId, Version.V4_0_0)) {
            throw new NotSupportedCassandraVersionException("It is available in Cassandra version 4.0 and later");
        }

        SimpleStatement statement = QueryBuilder
            .selectFrom(CassandraSystemKeyspace.SYSTEM_VIEWS.getKeyspaceName(), CassandraSystemTable.SYSTEM_VIEW_CLIENTS.getTableName())
            .all()
            .build();

        ResultSet rs = session.execute(statement);

        List<ClusterClient> clients = StreamSupport.stream(rs.spliterator(), false)
            .map(ClusterClient::from)
            .toList();

        return ClusterClientListResult.builder()
            .clients(clients)
            .nextPageState(rs.getExecutionInfo().getPagingState())
            .build();
    }

}
