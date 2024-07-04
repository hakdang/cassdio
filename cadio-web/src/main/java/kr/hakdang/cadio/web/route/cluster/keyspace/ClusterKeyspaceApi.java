package kr.hakdang.cadio.web.route.cluster.keyspace;

import kr.hakdang.cadio.core.domain.cluster.keyspace.ClusterKeyspaceListResult;
import kr.hakdang.cadio.web.common.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
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
public class ClusterKeyspaceApi {

    private final ClusterKeyspaceReader clusterKeyspaceReader;

    public ClusterKeyspaceApi(
        ClusterKeyspaceReader clusterKeyspaceReader
    ) {
        this.clusterKeyspaceReader = clusterKeyspaceReader;
    }

    @GetMapping("/keyspace")
    public ApiResponse<Map<String, Object>> keyspaceList(
        @PathVariable String clusterId
    ) {
        Map<String, Object> result = new HashMap<>();
        ClusterKeyspaceListResult response = clusterKeyspaceReader.listKeyspace(clusterId);
        result.put("keyspaceList", response.getKeyspaceList());
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
    public Map<String, Object> keyspaceDetail(
        @PathVariable String clusterId,
        @PathVariable String keyspace
    ) {
        Map<String, Object> result = new HashMap<>();
        String keyspaceDescribe = clusterKeyspaceReader.getKeyspace(clusterId, keyspace);
        result.put("describe", keyspaceDescribe);
        return result;
    }
}
