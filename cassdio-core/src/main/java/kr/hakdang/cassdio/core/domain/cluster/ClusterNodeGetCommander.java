package kr.hakdang.cassdio.core.domain.cluster;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.metadata.Node;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

/**
 * ClusterNodeListCommander
 *
 * @author seungh0
 * @since 2024-07-03
 */
@Service
public class ClusterNodeGetCommander extends BaseClusterCommander {

    public ClusterNode getNode(CqlSession session, UUID nodeId) {
        Map<UUID, Node> nodes = session.getMetadata().getNodes();

        Node node = nodes.get(nodeId);
        if (node == null) {
            throw new IllegalArgumentException(String.format("not exists node(%s)", nodeId));
        }

        return ClusterNode.from(node);
    }

}
