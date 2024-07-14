package kr.hakdang.cassdio.web.route.cluster.keyspace.table.pureselect;

import com.datastax.oss.driver.api.core.CqlSession;
import kr.hakdang.cassdio.core.domain.cluster.CqlSessionSelectResults;
import kr.hakdang.cassdio.core.domain.cluster.TempClusterConnector;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.ClusterTableCommander;
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
 * ClusterTablePureSelectApi
 *
 * @author akageun
 * @since 2024-07-03
 */
@Slf4j
@RestController
@RequestMapping("/api/cassandra/cluster/{clusterId}/keyspace/{keyspace}")
public class ClusterTablePureSelectApi {

    private final ClusterTableCommander clusterTableCommander;

    private final TempClusterConnector tempClusterConnector;

    public ClusterTablePureSelectApi(
        ClusterTableCommander clusterTableCommander,
        TempClusterConnector tempClusterConnector
    ) {
        this.clusterTableCommander = clusterTableCommander;
        this.tempClusterConnector = tempClusterConnector;
    }

    @PostMapping("/table/{table}/row")
    public ApiResponse<Map<String, Object>> tablePureTableSelect(
        @PathVariable String clusterId,
        @PathVariable String keyspace,
        @PathVariable String table,
        @RequestBody ClusterTablePureSelectRequest request
    ) {
        Map<String, Object> map = new HashMap<>();
        try (CqlSession session = tempClusterConnector.makeSession(clusterId)) { //TODO : interface 작업할 때 facade layer 로 변경 예정
            CqlSessionSelectResults result1 = clusterTableCommander.pureSelect(session, request.makeArgs(keyspace, table));

            map.put("nextCursor", result1.getNextCursor());
            map.put("rows", result1.getRows());
            map.put("columnHeader", result1.getColumnHeader());
        }

        return ApiResponse.ok(map);
    }
}
