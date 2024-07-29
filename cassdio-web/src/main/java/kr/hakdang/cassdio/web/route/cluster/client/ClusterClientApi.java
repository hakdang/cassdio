package kr.hakdang.cassdio.web.route.cluster.client;

import com.datastax.oss.driver.api.core.CqlSession;
import kr.hakdang.cassdio.core.domain.cluster.ClusterConnector;
import kr.hakdang.cassdio.core.domain.cluster.CqlSessionFactory;
import kr.hakdang.cassdio.core.domain.cluster.client.ClusterClientListCommander;
import kr.hakdang.cassdio.core.domain.cluster.client.ClusterClientListResult;
import kr.hakdang.cassdio.web.common.dto.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private CqlSessionFactory cqlSessionFactory;
    private final ClusterConnector clusterConnector;
    private final ClusterClientListCommander clusterClientListCommander;

    public ClusterClientApi(ClusterConnector clusterConnector, ClusterClientListCommander clusterClientListCommander) {
        this.clusterConnector = clusterConnector;
        this.clusterClientListCommander = clusterClientListCommander;
    }

    @GetMapping
    public ApiResponse<ClusterClientListResult> getClients(
        @PathVariable String clusterId
    ) {
        CqlSession session = cqlSessionFactory.get(clusterId);
        ClusterClientListResult result = clusterClientListCommander.getClients(session);
        return ApiResponse.ok(result);
    }

}
