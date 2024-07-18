package kr.hakdang.cassdio.web.route.cluster;

import com.datastax.oss.driver.api.core.CqlSession;
import jakarta.validation.Valid;
import kr.hakdang.cassdio.core.domain.cluster.ClusterConnector;
import kr.hakdang.cassdio.core.domain.cluster.info.ClusterInfo;
import kr.hakdang.cassdio.core.domain.cluster.info.ClusterInfoArgs;
import kr.hakdang.cassdio.core.domain.cluster.info.ClusterManager;
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

import static java.util.Collections.emptyMap;

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
    private final ClusterManager clusterManager;

    public ClusterApi(
        ClusterConnector clusterConnector,
        ClusterManager clusterManager
    ) {
        this.clusterConnector = clusterConnector;
        this.clusterManager = clusterManager;
    }

    @GetMapping("")
    public ApiResponse<Map<String, Object>> getCassandraClusterList(
        @RequestParam(required = false, defaultValue = "false") boolean withPassword
    ) {
        Map<String, Object> result = new HashMap<>();
        List<ClusterInfo> clusters = clusterManager.findAll();
        if (!withPassword) {
            clusters = clusters.stream()
                .map(info -> info.toBuilder().password(null).build())
                .toList();
        }

        result.put("clusters", clusters);

        return ApiResponse.ok(result);
    }

    @GetMapping("/{clusterId}")
    public ApiResponse<Map<String, Object>> getCassandraClusterDetail(
        @PathVariable String clusterId
    ) {
        Map<String, Object> result = new HashMap<>();
        result.put("cluster", clusterManager.findById(clusterId));

        return ApiResponse.ok(result);
    }

    @PostMapping("")
    public ApiResponse<Map<String, Object>> clusterRegister(
        @Valid @RequestBody ClusterRegisterRequest request
    ) {
        try (CqlSession session = clusterConnector.makeSession(request.makeClusterConnector())) {
            String clusterName = session.getMetadata().getClusterName()
                .orElse(UUID.randomUUID().toString());

            ClusterInfoArgs args = request.makeArgs(clusterName);
            //실행 안되면 exception

            clusterManager.register(args);
        }

        return ApiResponse.ok(emptyMap());
    }

    @PutMapping("/{clusterId}")
    public ApiResponse<Map<String, Object>> clusterUpdate(
        @PathVariable String clusterId,
        @Valid @RequestBody ClusterRegisterRequest request
    ) {
        try (CqlSession session = clusterConnector.makeSession(request.makeClusterConnector())) {
            String clusterName = session.getMetadata().getClusterName()
                .orElse(UUID.randomUUID().toString());

            ClusterInfoArgs args = request.makeArgs(clusterName);

            clusterManager.update(clusterId, args);
        }

        return ApiResponse.ok(emptyMap());
    }

    @DeleteMapping("/{clusterId}")
    public ApiResponse<Void> clusterDelete(
        @PathVariable String clusterId
    ) {

        clusterManager.deleteById(clusterId);

        return ApiResponse.ok();
    }


}
