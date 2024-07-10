package kr.hakdang.cassdio.web.route.cluster.keyspace;

import com.datastax.oss.driver.api.core.CqlSession;
import kr.hakdang.cassdio.core.domain.cluster.TempClusterConnector;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.ClusterKeyspaceCommander;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.ClusterKeyspaceDescribeArgs;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.ClusterKeyspaceListResult;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.ClusterDescTablesArgs;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.ClusterTableCommander;
import kr.hakdang.cassdio.web.common.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
public class ClusterKeyspaceApi {

    private final TempClusterConnector tempClusterConnector;
    private final ClusterKeyspaceReader clusterKeyspaceReader;
    private final ClusterTableCommander clusterTableCommander;
    private final ClusterKeyspaceCommander clusterKeyspaceCommander;

    public ClusterKeyspaceApi(
        TempClusterConnector tempClusterConnector,
        ClusterKeyspaceReader clusterKeyspaceReader,
        ClusterTableCommander clusterTableCommander,
        ClusterKeyspaceCommander clusterKeyspaceCommander
    ) {
        this.tempClusterConnector = tempClusterConnector;
        this.clusterKeyspaceReader = clusterKeyspaceReader;
        this.clusterTableCommander = clusterTableCommander;
        this.clusterKeyspaceCommander = clusterKeyspaceCommander;
    }

    @GetMapping("/keyspace")
    public ApiResponse<ClusterKeyspaceListResult> keyspaceList(
        @PathVariable String clusterId
    ) {
        return ApiResponse.ok(clusterKeyspaceReader.listKeyspace(clusterId));
    }

    @GetMapping("/keyspace-name")
    public ApiResponse<Map<String, Object>> keyspaceNames(
        @PathVariable String clusterId
    ) {
        Map<String, Object> result = new HashMap<>();
        result.put("keyspaceNameList", clusterKeyspaceReader.allKeyspaceNameList(clusterId));
        return ApiResponse.ok(result);
    }

    @PutMapping("/keyspace/cache-evict")
    public ApiResponse<Object> evictKeyspaceListCache(
        @PathVariable String clusterId
    ) {
        clusterKeyspaceReader.refreshKeyspaceCache(clusterId);
        return ApiResponse.OK;
    }

    @GetMapping("/keyspace/{keyspace}")
    public ApiResponse<Map<String, Object>> keyspaceDetail(
        @PathVariable String clusterId,
        @PathVariable String keyspace,
        @RequestParam(required = false, defaultValue = "false") boolean withTableList
    ) {
        Map<String, Object> result = new HashMap<>();

        //TODO : keyspace validation

        try (CqlSession session = tempClusterConnector.makeSession(clusterId, keyspace)) {
            result.put("describe", clusterKeyspaceCommander.describe(session, ClusterKeyspaceDescribeArgs.builder()
                .keyspace(keyspace)
                .withChildren(false)
                .pretty(true)
                .build()));

            if (withTableList) {
                result.put("tableList", clusterTableCommander.allTables(session, ClusterDescTablesArgs.builder()
                    .keyspace(keyspace)
                    .pageSize(50)
                    .build()));
            }
        }

        return ApiResponse.ok(result);
    }
}
