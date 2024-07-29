package kr.hakdang.cassdio.web.route.cluster.query;

import com.datastax.oss.driver.api.core.CqlSession;
import kr.hakdang.cassdio.core.domain.cluster.ClusterConnector;
import kr.hakdang.cassdio.core.domain.cluster.CqlSessionFactory;
import kr.hakdang.cassdio.core.domain.cluster.query.ClusterQueryCommander;
import kr.hakdang.cassdio.core.domain.cluster.query.QueryDTO;
import kr.hakdang.cassdio.web.common.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private CqlSessionFactory cqlSessionFactory;
    private final ClusterConnector clusterConnector;
    private final ClusterQueryCommander clusterQueryCommander;

    public ClusterQueryApi(
        ClusterConnector clusterConnector,
        ClusterQueryCommander clusterQueryCommander
    ) {
        this.clusterConnector = clusterConnector;
        this.clusterQueryCommander = clusterQueryCommander;
    }

    @PostMapping(value = {"/{clusterId}/query", "/{clusterId}/keyspace/{keyspace}/query"})
    public ApiResponse<Map<String, Object>> clusterQueryCommand(
        @PathVariable String clusterId,
        @PathVariable(required = false) String keyspace,
        @RequestBody ClusterQueryRequest request
    ) {
        Map<String, Object> responseMap = new HashMap<>();

        CqlSession session = cqlSessionFactory.get(clusterId, keyspace);
        QueryDTO.ClusterQueryCommanderResult result = clusterQueryCommander.execute(session, request.makeArgs());

        responseMap.put("wasApplied", result.isWasApplied());
        responseMap.put("nextCursor", result.getNextCursor());
        responseMap.put("rows", result.getRows());
        responseMap.put("rowHeader", result.getRowHeader());
        if (request.isTrace()) {
            responseMap.put("queryTrace", result.getQueryTrace());
        }

        return ApiResponse.ok(responseMap);
    }
}
