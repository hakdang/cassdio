package kr.hakdang.cassdio.web.route.cluster.keyspace;

import com.datastax.oss.driver.api.core.CqlSession;
import kr.hakdang.cassdio.core.domain.cluster.ClusterConnector;
import kr.hakdang.cassdio.core.domain.cluster.ClusterUtils;
import kr.hakdang.cassdio.core.domain.cluster.CqlSessionSelectResults;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.ClusterKeyspaceCommander;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.KeyspaceDTO;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.ClusterTableListArgs;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.ClusterTableCommander;
import kr.hakdang.cassdio.web.common.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
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

    private final ClusterConnector clusterConnector;
    private final ClusterTableCommander clusterTableCommander;
    private final ClusterKeyspaceCommander clusterKeyspaceCommander;

    public ClusterKeyspaceApi(
        ClusterConnector clusterConnector,
        ClusterTableCommander clusterTableCommander,
        ClusterKeyspaceCommander clusterKeyspaceCommander
    ) {
        this.clusterConnector = clusterConnector;
        this.clusterTableCommander = clusterTableCommander;
        this.clusterKeyspaceCommander = clusterKeyspaceCommander;
    }

    @GetMapping("/keyspace")
    public ApiResponse<KeyspaceDTO.ClusterKeyspaceListResult> keyspaceList(
        @PathVariable String clusterId
    ) {
        try (CqlSession session = clusterConnector.makeSession(clusterId)) {
            return ApiResponse.ok(clusterKeyspaceCommander.generalKeyspaceList(session));
        }

    }

    @GetMapping("/keyspace-name")
    public ApiResponse<Map<String, Object>> keyspaceNames(
        @PathVariable String clusterId
    ) {
        Map<String, Object> result = new HashMap<>();
        try (CqlSession session = clusterConnector.makeSession(clusterId)) {
            List<KeyspaceDTO.KeyspaceNameResult> allKeyspaceList = clusterKeyspaceCommander.allKeyspaceNameList(session);
            result.put("keyspaceNameList", allKeyspaceList);
        }

        return ApiResponse.ok(result);
    }

    @GetMapping("/keyspace/{keyspace}")
    public ApiResponse<Map<String, Object>> keyspaceDetail(
        @PathVariable String clusterId,
        @PathVariable String keyspace,
        @RequestParam(required = false, defaultValue = "false") boolean withTableList
    ) {
        Map<String, Object> result = new HashMap<>();

        //TODO : keyspace validation
        try (CqlSession session = clusterConnector.makeSession(clusterId, keyspace)) {
            var describeArgs = KeyspaceDTO.ClusterKeyspaceDescribeArgs.builder()
                .keyspace(keyspace)
                .withChildren(false)
                .pretty(true)
                .build();

            result.put("describe", clusterKeyspaceCommander.describe(session, describeArgs));
            result.put("detail", clusterKeyspaceCommander.keyspaceDetail(session, keyspace));

            if (withTableList) { //TODO 해당 값 외에 view 등의 기능은 탭을 생성하여 화면 이동하면 호출할 수 있도록 개발 예정
                ClusterTableListArgs args = ClusterTableListArgs.builder()
                    .keyspace(keyspace)
                    .pageSize(100)
                    .build();

                CqlSessionSelectResults tableListResult = clusterTableCommander.allTables(session, args);

                result.put("tableList", tableListResult);
            }
        }

        return ApiResponse.ok(result);
    }

    @DeleteMapping("/keyspace/{keyspace}")
    public ApiResponse<Void> keyspaceDrop(
        @PathVariable String clusterId,
        @PathVariable String keyspace
    ) {

        //TODO : keyspace validation
        try (CqlSession session = clusterConnector.makeSession(clusterId, keyspace)) {
            if (ClusterUtils.isSystemKeyspace(session.getContext(), keyspace)) {
                throw new IllegalArgumentException("System Keyspace!");
            }

            clusterKeyspaceCommander.keyspaceDrop(session, keyspace);
        }

        return ApiResponse.ok();
    }

}
