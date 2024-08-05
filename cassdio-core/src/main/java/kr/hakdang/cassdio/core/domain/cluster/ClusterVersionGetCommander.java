package kr.hakdang.cassdio.core.domain.cluster;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.Version;
import com.datastax.oss.driver.api.core.metadata.Node;
import com.datastax.oss.driver.internal.core.channel.DriverChannel;
import com.datastax.oss.driver.internal.core.context.InternalDriverContext;
import kr.hakdang.cassdio.core.domain.cluster.ClusterException.ClusterNodeNotFoundException;
import org.springframework.stereotype.Service;

/**
 * ClusterVersionGetCommander
 *
 * @author seungh0
 * @since 2024-07-25
 */
@Service
public class ClusterVersionGetCommander extends BaseClusterCommander {

    public ClusterVersionGetCommander(
        CqlSessionFactory cqlSessionFactory
    ) {
        this.cqlSessionFactory = cqlSessionFactory;
    }

    public Version getCassandraVersion(String clusterId) {
        CqlSession session = cqlSessionFactory.get(clusterId);

        return getCassandraVersionWithSession(session);
    }

    public Version getCassandraVersionWithSession(CqlSession session) {
        DriverChannel channel = ((InternalDriverContext) session.getContext()).getControlConnection().channel();
        Node node = session.getMetadata().findNode(channel.getEndPoint())
            .orElseThrow(() -> new ClusterNodeNotFoundException("Any live node in cluster"));
        return node.getCassandraVersion();
    }

}
