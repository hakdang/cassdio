package kr.hakdang.cassdio.web.route.cluster.keyspace;

import com.datastax.oss.driver.api.core.CqlSession;
import kr.hakdang.cassdio.common.CassdioConstants;
import kr.hakdang.cassdio.core.domain.cluster.ClusterConnector;
import kr.hakdang.cassdio.core.domain.cluster.ClusterUtils;
import kr.hakdang.cassdio.core.domain.cluster.CqlSessionFactory;
import kr.hakdang.cassdio.core.domain.cluster.CqlSessionSelectResults;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.ClusterKeyspaceCommander;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.ClusterKeyspaceProvider;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.KeyspaceDTO;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.ClusterTableListCommander;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.TableDTO;
import kr.hakdang.cassdio.web.common.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private CqlSessionFactory cqlSessionFactory;
    private final ClusterConnector clusterConnector;
    private final ClusterKeyspaceProvider clusterKeyspaceProvider;
    private final ClusterKeyspaceCommander clusterKeyspaceCommander;
    private final ClusterTableListCommander clusterTableListCommander;

    public ClusterKeyspaceApi(
        ClusterConnector clusterConnector, ClusterKeyspaceProvider clusterKeyspaceProvider,
        ClusterKeyspaceCommander clusterKeyspaceCommander,
        ClusterTableListCommander clusterTableListCommander
    ) {
        this.clusterConnector = clusterConnector;
        this.clusterKeyspaceProvider = clusterKeyspaceProvider;
        this.clusterKeyspaceCommander = clusterKeyspaceCommander;
        this.clusterTableListCommander = clusterTableListCommander;
    }

    @GetMapping("/keyspace")
    public ApiResponse<KeyspaceDTO.ClusterKeyspaceListResult> keyspaceList(
        @PathVariable(name = CassdioConstants.CLUSTER_ID_PATH) String clusterId
    ) {
        CqlSession session = cqlSessionFactory.get(clusterId);
        return ApiResponse.ok(clusterKeyspaceCommander.generalKeyspaceList(session));

    }

    @GetMapping("/keyspace-name")
    public ApiResponse<Map<String, Object>> keyspaceNames(
        @PathVariable(name = CassdioConstants.CLUSTER_ID_PATH) String clusterId,
        @RequestParam(required = false, defaultValue = "false") boolean cacheEvict
    ) {
        Map<String, Object> responseMap = new HashMap<>();
        CqlSession session = cqlSessionFactory.get(clusterId);
        if (cacheEvict) {
            clusterKeyspaceProvider.keyspaceNameListCacheEvict(clusterId);
        }

        List<KeyspaceDTO.KeyspaceNameResult> allKeyspaceList = clusterKeyspaceProvider.keyspaceNameResultList(session, clusterId);
        responseMap.put("keyspaceNameList", allKeyspaceList);

        return ApiResponse.ok(responseMap);
    }

    @GetMapping("/keyspace/{keyspace}")
    public ApiResponse<Map<String, Object>> keyspaceDetail(
        @PathVariable(name = CassdioConstants.CLUSTER_ID_PATH) String clusterId,
        @PathVariable String keyspace,
        @RequestParam(required = false, defaultValue = "false") boolean withTableList
    ) {
        Map<String, Object> responseMap = new HashMap<>();

        //TODO : keyspace validation
        CqlSession session = cqlSessionFactory.get(clusterId, keyspace);
        var describeArgs = KeyspaceDTO.ClusterKeyspaceDescribeArgs.builder()
            .keyspace(keyspace)
            .withChildren(false)
            .pretty(true)
            .build();

        responseMap.put("describe", clusterKeyspaceCommander.describe(session, describeArgs));
        responseMap.put("detail", clusterKeyspaceCommander.keyspaceDetail(session, keyspace));

        if (withTableList) { //TODO 해당 값 외에 view 등의 기능은 탭을 생성하여 화면 이동하면 호출할 수 있도록 개발 예정
            TableDTO.ClusterTableListArgs args = TableDTO.ClusterTableListArgs.builder()
                .keyspace(keyspace)
                .pageSize(100)
                .build();

            CqlSessionSelectResults tableListResult = clusterTableListCommander.tableList(session, args);

            responseMap.put("tableList", tableListResult);
        }

        return ApiResponse.ok(responseMap);
    }

    @DeleteMapping("/keyspace/{keyspace}")
    public ApiResponse<Void> keyspaceDrop(
        @PathVariable(name = CassdioConstants.CLUSTER_ID_PATH) String clusterId,
        @PathVariable String keyspace
    ) {

        //TODO : keyspace validation
        CqlSession session = cqlSessionFactory.get(clusterId, keyspace);
        if (ClusterUtils.isSystemKeyspace(session.getContext(), keyspace)) {
            throw new IllegalArgumentException("System Keyspace!");
        }

        clusterKeyspaceCommander.keyspaceDrop(session, keyspace);

        return ApiResponse.ok();
    }

}
