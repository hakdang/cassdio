package kr.hakdang.cassdio.web.route.cluster.client;

import com.datastax.oss.driver.api.core.CqlSession;
import kr.hakdang.cassdio.core.domain.cluster.ClusterConnector;
import kr.hakdang.cassdio.core.domain.cluster.client.CassandraClientListCommander;
import kr.hakdang.cassdio.core.domain.cluster.client.CassandraClientListResult;
import kr.hakdang.cassdio.web.common.dto.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * CassandraClientListApi
 *
 * @author seungh0
 * @since 2024-07-25
 */
@RequestMapping("/api/cassandra/cluster/{clusterId}/client")
@RestController
public class CassandraClientListApi {

    private final ClusterConnector clusterConnector;
    private final CassandraClientListCommander cassandraClientListCommander;

    public CassandraClientListApi(ClusterConnector clusterConnector, CassandraClientListCommander cassandraClientListCommander) {
        this.clusterConnector = clusterConnector;
        this.cassandraClientListCommander = cassandraClientListCommander;
    }

    @GetMapping
    public ApiResponse<CassandraClientListResult> getCassandraClients(
        @PathVariable String clusterId
    ) {
        try (CqlSession session = clusterConnector.makeSession(clusterId)) {
            CassandraClientListResult result = cassandraClientListCommander.getClients(session);
            return ApiResponse.ok(result);
        }
    }

}
