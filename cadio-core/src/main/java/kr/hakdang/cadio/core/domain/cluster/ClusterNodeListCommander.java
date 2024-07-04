package kr.hakdang.cadio.core.domain.cluster;

import com.datastax.oss.driver.api.core.CqlSession;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ClusterNodeListCommander
 *
 * @author seungh0
 * @since 2024-07-03
 */
@Service
public class ClusterNodeListCommander extends BaseClusterCommander {

    public List<ClusterNode> listNodes(CqlSession session) {
        return session.getMetadata().getNodes().values().stream()
            .map(ClusterNode::from)
            .sorted(Comparator.comparing(ClusterNode::getDatacenter)
                .thenComparing(ClusterNode::getRack))
            .collect(Collectors.toList());
    }

}
