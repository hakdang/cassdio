package kr.hakdang.cadio.web.route.cluster.table.pureselect;

import com.datastax.oss.driver.api.core.CqlSession;
import kr.hakdang.cadio.core.domain.cluster.TempClusterConnector;
import kr.hakdang.cadio.core.domain.cluster.keyspace.table.ClusterTableCommander;
import kr.hakdang.cadio.core.domain.cluster.keyspace.table.ClusterTablePureSelectResult;
import kr.hakdang.cadio.web.common.dto.response.ApiResponse;
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
 * ClusterTablePureSelectApi
 *
 * @author akageun
 * @since 2024-07-03
 */
@Slf4j
@RestController
@RequestMapping("/api/cassandra/cluster/{clusterId}/keyspace/{keyspace}")
public class ClusterTablePureSelectApi {

    @Autowired
    private ClusterTableCommander clusterTableCommander;

    @Autowired
    private TempClusterConnector tempClusterConnector;

    @PostMapping("/table/{table}/query")
    public ApiResponse<Map<String, Object>> clusterQueryCommand(
        @PathVariable String clusterId,
        @PathVariable String keyspace,
        @PathVariable String table,
        @RequestBody ClusterTablePureSelectRequest request
    ) {
        Map<String, Object> map = new HashMap<>();
        try (CqlSession session = tempClusterConnector.makeSession(clusterId)) { //TODO : interface 작업할 때 facade layer 로 변경 예정
            ClusterTablePureSelectResult result1 = clusterTableCommander.pureSelect(session, request.makeArgs(keyspace, table));

            map.put("wasApplied", result1.isWasApplied());
            map.put("nextCursor", result1.getNextCursor());
            map.put("rows", result1.getRows());
            map.put("columnNames", result1.getColumnNames());
        } catch (Exception e) {
            log.error("error : {}", e.getMessage(), e);
            throw e;
        }

        return ApiResponse.ok(map);
    }
}
