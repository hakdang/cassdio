package kr.hakdang.cassdio.core.domain.cluster.node;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.metadata.Node;
import kr.hakdang.cassdio.core.domain.cluster.BaseClusterCommander;
import kr.hakdang.cassdio.core.domain.cluster.ClusterException.ClusterNodeNotFoundException;
import kr.hakdang.cassdio.core.domain.cluster.CqlSessionFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

/**
 * ClusterNodeGetCommander
 *
 * @author seungh0
 * @since 2024-07-03
 */
@Service
public class ClusterNodeGetCommander extends BaseClusterCommander {

    private final CqlSessionFactory cqlSessionFactory;

    public ClusterNodeGetCommander(
        CqlSessionFactory cqlSessionFactory
    ) {
        this.cqlSessionFactory = cqlSessionFactory;
    }

    public ClusterNode getNode(String clusterId, UUID nodeId) {
        CqlSession session = cqlSessionFactory.get(clusterId);

        Map<UUID, Node> nodes = session.getMetadata().getNodes();

        Node node = nodes.get(nodeId);
        if (node == null) {
            throw new ClusterNodeNotFoundException(String.format("not exists node(%s)", nodeId));
        }

        return ClusterNode.from(node);
    }

}
