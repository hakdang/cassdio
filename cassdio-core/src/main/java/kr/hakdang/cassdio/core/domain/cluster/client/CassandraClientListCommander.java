package kr.hakdang.cassdio.core.domain.cluster.client;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.Version;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import kr.hakdang.cassdio.core.domain.cluster.BaseClusterCommander;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.CassandraSystemKeyspace;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.CassandraSystemTable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * CassandraClientListCommander
 *
 * @author seungh0
 * @since 2024-07-25
 */
@Service
public class CassandraClientListCommander extends BaseClusterCommander {

    public CassandraClientListResult getClients(CqlSession session) {
        Version version = getCassandraVersion(session);
        if (version.compareTo(Version.V4_0_0) < 0) {
            throw new IllegalStateException("Client lookup feature is available in Cassandra version 4.0 and later");
        }

        SimpleStatement statement = QueryBuilder
            .selectFrom(CassandraSystemKeyspace.SYSTEM_VIEWS.getKeyspaceName(), CassandraSystemTable.SYSTEM_VIEW_CLIENTS.getTableName())
            .all()
            .build();

        ResultSet rs = session.execute(statement);

        List<CassandraClient> clients = StreamSupport.stream(rs.spliterator(), false)
            .map(CassandraClient::from)
            .collect(Collectors.toList());

        return CassandraClientListResult.builder()
            .clients(clients)
            .nextPageState(rs.getExecutionInfo().getPagingState())
            .build();
    }

}
