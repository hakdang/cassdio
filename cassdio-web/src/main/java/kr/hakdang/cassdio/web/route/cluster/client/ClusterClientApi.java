package kr.hakdang.cassdio.web.route.cluster.client;

import kr.hakdang.cassdio.core.domain.cluster.client.ClusterClientListCommander;
import kr.hakdang.cassdio.core.domain.cluster.client.ClusterClientListResult;
import kr.hakdang.cassdio.web.common.dto.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClusterClientApi
 *
 * @author seungh0
 * @since 2024-07-25
 */
@RequestMapping("/api/cassandra/cluster/{clusterId}/client")
@RestController
public class ClusterClientApi {
    private final ClusterClientListCommander clusterClientListCommander;

    public ClusterClientApi(ClusterClientListCommander clusterClientListCommander) {
        this.clusterClientListCommander = clusterClientListCommander;
    }

    @GetMapping
    public ApiResponse<ClusterClientListResult> getClients(
        @PathVariable String clusterId
    ) {
        ClusterClientListResult result = clusterClientListCommander.getClients(clusterId);
        return ApiResponse.ok(result);
    }

}
