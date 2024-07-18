package kr.hakdang.cassdio.web.route.cluster.query;

import com.datastax.oss.driver.api.core.CqlSession;
import kr.hakdang.cassdio.core.domain.cluster.ClusterQueryCommander;
import kr.hakdang.cassdio.core.domain.cluster.ClusterQueryCommanderResult;
import kr.hakdang.cassdio.core.domain.cluster.ClusterConnector;
import kr.hakdang.cassdio.web.common.dto.response.ApiResponse;
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
public class ClusterQueryApi {

    private final ClusterConnector clusterConnector;
    private final ClusterQueryCommander clusterQueryCommander;

    public ClusterQueryApi(
        ClusterConnector clusterConnector,
        ClusterQueryCommander clusterQueryCommander
    ) {
        this.clusterConnector = clusterConnector;
        this.clusterQueryCommander = clusterQueryCommander;
    }

    @PostMapping("/{clusterId}/query")
    public ApiResponse<Map<String, Object>> clusterQueryCommand(
        @PathVariable String clusterId,
        @RequestBody ClusterQueryRequest request
    ) {
        Map<String, Object> map = new HashMap<>();

        try (CqlSession session = clusterConnector.makeSession(clusterId)) { //TODO : interface 작업할 때 facade layer 로 변경 예정
            ClusterQueryCommanderResult result1 = clusterQueryCommander.execute(session, request.makeArgs());

            map.put("wasApplied", result1.isWasApplied());
            map.put("nextCursor", result1.getNextCursor());
            map.put("rows", result1.getRows());
            map.put("columnNames", result1.getColumnNames()); //TODO 변경 예정
        }

        return ApiResponse.ok(map);
    }
}
