package kr.hakdang.cadio.web.route.cluster.keyspace;

import com.datastax.oss.driver.api.core.CqlSession;
import kr.hakdang.cadio.core.domain.cluster.TempClusterConnector;
import kr.hakdang.cadio.core.domain.cluster.keyspace.ClusterKeyspaceCommander;
import kr.hakdang.cadio.core.domain.cluster.keyspace.ClusterKeyspaceDescribeArgs;
import kr.hakdang.cadio.core.domain.cluster.keyspace.ClusterKeyspaceListResult;
import kr.hakdang.cadio.web.common.dto.response.ApiResponse;
import kr.hakdang.cadio.web.route.BaseSample;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * ClusterKeyspaceApi
 *
 * @author akageun
 * @since 2024-06-30
 */
@Slf4j
@RestController
@RequestMapping("/api/cassandra/cluster/{clusterId}")
public class ClusterKeyspaceApi extends BaseSample {

    private final TempClusterConnector tempClusterConnector;
    private final ClusterKeyspaceCommander clusterKeyspaceCommander;

    public ClusterKeyspaceApi(
        TempClusterConnector tempClusterConnector,
        ClusterKeyspaceCommander clusterKeyspaceCommander
    ) {
        this.tempClusterConnector = tempClusterConnector;
        this.clusterKeyspaceCommander = clusterKeyspaceCommander;
    }

    @GetMapping("/keyspace")
    public ApiResponse<Map<String, Object>> keyspaceList(
        @PathVariable String clusterId
    ) {
        Map<String, Object> result = new HashMap<>();
        try (CqlSession session = tempClusterConnector.makeSession(clusterId)) { //TODO : interface 작업할 때 facade layer 로 변경 예정
            ClusterKeyspaceListResult result1 = clusterKeyspaceCommander.keyspaceList(session);

            result.put("keyspaceList", result1.getKeyspaceList());
        }

        return ApiResponse.ok(result);
    }

    @GetMapping("/keyspace/{keyspace}")
    public Map<String, Object> keyspaceDetail(
        @PathVariable String clusterId,
        @PathVariable String keyspace
    ) {
        Map<String, Object> result = new HashMap<>();
        try (CqlSession session = tempClusterConnector.makeSession(clusterId)) { //TODO : interface 작업할 때 facade layer 로 변경 예정
            result.put("describe", clusterKeyspaceCommander.describe(session, ClusterKeyspaceDescribeArgs.builder()
                .keyspace(keyspace)
                .withChildren(false)
                .pretty(true)
                .build()));

        }

        return result;
    }
}
