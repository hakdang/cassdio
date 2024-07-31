package kr.hakdang.cassdio.core.domain.cluster.node;

import com.datastax.oss.driver.api.core.CqlSession;
import kr.hakdang.cassdio.core.domain.cluster.BaseClusterCommander;
import kr.hakdang.cassdio.core.domain.cluster.CqlSessionFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * ClusterNodeListCommander
 *
 * @author seungh0
 * @since 2024-07-03
 */
@Service
public class ClusterNodeListCommander extends BaseClusterCommander {

    public ClusterNodeListCommander(
        CqlSessionFactory cqlSessionFactory
    ) {
        this.cqlSessionFactory = cqlSessionFactory;
    }

    public List<ClusterNode> listNodes(String clusterId) {
        CqlSession session = cqlSessionFactory.get(clusterId);

        return session.getMetadata().getNodes().values().stream()
            .map(ClusterNode::from)
            .sorted(Comparator.comparing(ClusterNode::getDatacenter)
                .thenComparing(ClusterNode::getRack))
            .toList();
    }

}
