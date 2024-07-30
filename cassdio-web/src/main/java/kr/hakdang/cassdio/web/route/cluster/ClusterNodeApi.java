package kr.hakdang.cassdio.web.route.cluster;

import kr.hakdang.cassdio.core.domain.cluster.node.ClusterNode;
import kr.hakdang.cassdio.core.domain.cluster.node.ClusterNodeGetCommander;
import kr.hakdang.cassdio.core.domain.cluster.node.ClusterNodeListCommander;
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
    private final ClusterNodeListCommander clusterNodeListCommander;
    private final ClusterNodeGetCommander clusterNodeGetCommander;

    public ClusterNodeApi(
        ClusterNodeListCommander clusterNodeListCommander,
        ClusterNodeGetCommander clusterNodeGetCommander
    ) {
        this.clusterNodeListCommander = clusterNodeListCommander;
        this.clusterNodeGetCommander = clusterNodeGetCommander;
    }

    @GetMapping("/node")
    public ApiResponse<ItemListWithCursorResponse<ClusterNode, String>> nodeList(
        @PathVariable String clusterId
    ) {
        List<ClusterNode> nodes = clusterNodeListCommander.listNodes(clusterId);
        return ApiResponse.ok(ItemListWithCursorResponse.noMore(nodes));
    }

    @GetMapping("/node/{nodeId}")
    public ApiResponse<ClusterNode> nodeDetail(
        @PathVariable String clusterId,
        @PathVariable UUID nodeId
    ) {
        ClusterNode result = clusterNodeGetCommander.getNode(clusterId, nodeId);
        return ApiResponse.ok(result);
    }

}
