package kr.hakdang.cassdio.web.route.cluster;

import com.datastax.oss.driver.api.core.CqlSession;
import jakarta.validation.Valid;
import kr.hakdang.cassdio.common.CassdioConstants;
import kr.hakdang.cassdio.core.domain.cluster.ClusterConnector;
import kr.hakdang.cassdio.core.domain.cluster.ClusterProvider;
import kr.hakdang.cassdio.core.domain.cluster.info.ClusterInfo;
import kr.hakdang.cassdio.core.domain.cluster.info.ClusterInfoArgs;
import kr.hakdang.cassdio.web.common.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * ClusterApi
 *
 * @author akageun
 * @since 2024-06-30
 */
@Slf4j
@RestController
@RequestMapping("/api/cassandra/cluster")
public class ClusterApi {

    private final ClusterConnector clusterConnector;
    private final ClusterProvider clusterProvider;

    public ClusterApi(
        ClusterConnector clusterConnector,
        ClusterProvider clusterProvider
    ) {
        this.clusterConnector = clusterConnector;
        this.clusterProvider = clusterProvider;
    }

    @GetMapping("")
    public ApiResponse<Map<String, Object>> clusterList(
        @RequestParam(required = false, defaultValue = "false") boolean withPassword
    ) {
        Map<String, Object> responseMap = new HashMap<>();

        List<ClusterInfo> clusters = clusterProvider.findAll();
        if (!withPassword) {
            clusters = clusters.stream()
                .map(info -> info.toBuilder().password(null).build())
                .toList();
        }

        responseMap.put("clusters", clusters);

        return ApiResponse.ok(responseMap);
    }

    @GetMapping("/{clusterId}")
    public ApiResponse<Map<String, Object>> clusterDetail(
        @PathVariable(name = CassdioConstants.CLUSTER_ID_PATH) String clusterId
    ) {
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("cluster", clusterProvider.findByIdWithoutCache(clusterId));

        return ApiResponse.ok(responseMap);
    }

    @PostMapping("")
    public ApiResponse<Void> clusterRegister(
        @Valid @RequestBody ClusterRegisterRequest request
    ) {
        try (CqlSession session = clusterConnector.makeSession(request.makeClusterConnector())) {
            String clusterName = session.getMetadata().getClusterName()
                .orElse(UUID.randomUUID().toString());

            ClusterInfoArgs args = request.makeArgs(clusterName);
            //실행 안되면 exception

            clusterProvider.register(args);
        }

        return ApiResponse.ok();
    }

    @PutMapping("/{clusterId}")
    public ApiResponse<Void> clusterUpdate(
        @PathVariable(name = CassdioConstants.CLUSTER_ID_PATH) String clusterId,
        @Valid @RequestBody ClusterRegisterRequest request
    ) {
        try (CqlSession session = clusterConnector.makeSession(request.makeClusterConnector())) {
            String clusterName = session.getMetadata().getClusterName()
                .orElse(UUID.randomUUID().toString());

            ClusterInfoArgs args = request.makeArgs(clusterName);

            clusterProvider.updateById(clusterId, args);
        }

        return ApiResponse.ok();
    }

    @DeleteMapping("/{clusterId}")
    public ApiResponse<Void> clusterDelete(
        @PathVariable(name = CassdioConstants.CLUSTER_ID_PATH) String clusterId
    ) {
        clusterProvider.deleteById(clusterId);

        return ApiResponse.ok();
    }
}
