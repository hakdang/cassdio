package kr.hakdang.cassdio.core.domain.cluster;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.Version;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.metadata.Node;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.internal.core.channel.DriverChannel;
import com.datastax.oss.driver.internal.core.context.InternalDriverContext;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.CassandraSystemKeyspace;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.CassandraSystemTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ClusterVersionCommander
 *
 * @author seungh0
 * @since 2024-07-25
 */
@Service
public class ClusterVersionCommander extends BaseClusterCommander {

    private final CqlSessionFactory cqlSessionFactory;

    public ClusterVersionCommander(
        CqlSessionFactory cqlSessionFactory
    ) {
        this.cqlSessionFactory = cqlSessionFactory;
    }

    public Version getCassandraVersion(String clusterId) {
        CqlSession session = cqlSessionFactory.get(clusterId);

        DriverChannel channel = ((InternalDriverContext) session.getContext()).getControlConnection().channel();
        Node node = session.getMetadata().findNode(channel.getEndPoint())
            .orElseThrow();//TODO : node not found exception 처리

        return node.getCassandraVersion();
    }

}
