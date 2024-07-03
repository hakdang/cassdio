package kr.hakdang.cadio.web.route.cluster.query;

import com.datastax.oss.driver.api.core.CqlSession;
import kr.hakdang.cadio.core.domain.cluster.ClusterQueryCommander;
import kr.hakdang.cadio.core.domain.cluster.ClusterQueryCommanderResult;
import kr.hakdang.cadio.core.domain.cluster.TempClusterConnector;
import kr.hakdang.cadio.web.common.dto.response.ApiResponse;
import kr.hakdang.cadio.web.route.BaseSample;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * ClusterQueryApi
 *
 * @author akageun
 * @since 2024-07-03
 */
@Slf4j
@RestController
@RequestMapping("/api/cassandra/cluster")
public class ClusterQueryApi extends BaseSample {

    private final TempClusterConnector tempClusterConnector;
    private final ClusterQueryCommander clusterQueryCommander;

    public ClusterQueryApi(
        TempClusterConnector tempClusterConnector,
        ClusterQueryCommander clusterQueryCommander
    ) {
        this.tempClusterConnector = tempClusterConnector;
        this.clusterQueryCommander = clusterQueryCommander;
    }

    @PostMapping("/{clusterId}/query")
    public ApiResponse<Map<String, Object>> clusterQueryCommand(
        @PathVariable String clusterId,
        @RequestBody ClusterQueryRequest request
    ) {
        Map<String, Object> map = new HashMap<>();

        try (CqlSession session = tempClusterConnector.makeSession(clusterId)) { //TODO : interface 작업할 때 facade layer 로 변경 예정
            ClusterQueryCommanderResult result1 = clusterQueryCommander.execute(session, request.makeArgs());

            map.put("wasApplied", result1.isWasApplied());
            map.put("nextCursor", result1.getNextCursor());
            map.put("rows", result1.getRows());
            map.put("columnNames", result1.getColumnNames());
        }

        return ApiResponse.ok(map);
    }
}
