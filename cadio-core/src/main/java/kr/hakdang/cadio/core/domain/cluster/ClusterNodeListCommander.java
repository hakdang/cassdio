package kr.hakdang.cadio.core.domain.cluster;

import com.datastax.oss.driver.api.core.CqlSession;
import org.springframework.stereotype.Service;

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
            .sorted((o1, o2) -> {
                if (o1.getDatacenter().compareTo(o2.getDatacenter()) < 0) {
                    return -1;
                } else if (o1.getDatacenter().compareTo(o2.getDatacenter()) > 0) {
                    return 1;
                }
                return o1.getRack().compareTo(o2.getRack());
            })
            .collect(Collectors.toList());
    }

}
