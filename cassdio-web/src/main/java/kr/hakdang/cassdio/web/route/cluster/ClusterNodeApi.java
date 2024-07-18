package kr.hakdang.cassdio.web.route.cluster;

import com.datastax.oss.driver.api.core.CqlSession;
import kr.hakdang.cassdio.core.domain.cluster.ClusterNode;
import kr.hakdang.cassdio.core.domain.cluster.ClusterNodeGetCommander;
import kr.hakdang.cassdio.core.domain.cluster.ClusterNodeListCommander;
import kr.hakdang.cassdio.core.domain.cluster.ClusterConnector;
import kr.hakdang.cassdio.web.common.dto.response.ApiResponse;
import kr.hakdang.cassdio.web.common.dto.response.ItemListWithCursorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * ClusterNodeApi
 *
 * @author akageun
 * @since 2024-06-30
 */
@RestController
@RequestMapping("/api/cassandra/cluster/{clusterId}")
public class ClusterNodeApi {

    private final ClusterConnector clusterConnector;
    private final ClusterNodeListCommander clusterNodeListCommander;
    private final ClusterNodeGetCommander clusterNodeGetCommander;

    public ClusterNodeApi(
        ClusterConnector clusterConnector,
        ClusterNodeListCommander clusterNodeListCommander,
        ClusterNodeGetCommander clusterNodeGetCommander
    ) {
        this.clusterConnector = clusterConnector;
        this.clusterNodeListCommander = clusterNodeListCommander;
        this.clusterNodeGetCommander = clusterNodeGetCommander;
    }

    @GetMapping("/node")
    public ApiResponse<ItemListWithCursorResponse<ClusterNode, String>> nodeList(
        @PathVariable String clusterId
    ) {
        try (CqlSession session = clusterConnector.makeSession(clusterId)) {
            List<ClusterNode> nodes = clusterNodeListCommander.listNodes(session);
            return ApiResponse.ok(ItemListWithCursorResponse.noMore(nodes));
        }
    }

    @GetMapping("/node/{nodeId}")
    public ApiResponse<ClusterNode> nodeDetail(
        @PathVariable String clusterId,
        @PathVariable UUID nodeId
    ) {
        try (CqlSession session = clusterConnector.makeSession(clusterId)) {
            ClusterNode result = clusterNodeGetCommander.getNode(session, nodeId);
            return ApiResponse.ok(result);
        }
    }

}
