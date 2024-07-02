package kr.hakdang.cadio.web.route.cluster;

import com.datastax.oss.driver.api.core.CqlSession;
import jakarta.validation.Valid;
import kr.hakdang.cadio.core.domain.cluster.info.ClusterInfoManager;
import kr.hakdang.cadio.core.domain.cluster.info.ClusterInfoProvider;
import kr.hakdang.cadio.web.common.dto.response.ApiResponse;
import kr.hakdang.cadio.web.route.BaseSample;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
public class ClusterApi extends BaseSample {

    @Autowired
    private ClusterInfoProvider clusterInfoProvider;

    @Autowired
    private ClusterInfoManager clusterInfoManager;

    @GetMapping("")
    public Map<String, Object> getCassandraClusterList() {
        return emptyMap();
    }

    @GetMapping("/{clusterId}")
    public Map<String, Object> getCassandraClusterDetail(
        @PathVariable String clusterId
    ) {
        Map<String, Object> result = new HashMap<>();
        try (CqlSession session = makeSession()) { //TODO : interface 작업할 때 facade layer 로 변경 예정
            //session.getMetadata().getNodes()
        } catch (Exception e) {
            log.error("error : {}", e.getMessage(), e);
            throw e;
        }
        return emptyMap();
    }

    @PostMapping("")
    public ApiResponse<Map<String, Object>> clusterRegister(
        @RequestBody ClusterRegisterRequest request
    ) {
        clusterInfoManager.register(
            request.contactPoints, request.port, request.localDatacenter, request.username, request.password
        );

        return ApiResponse.ok(emptyMap());
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ClusterRegisterRequest {
        private String contactPoints;
        private int port;
        private String localDatacenter;
        private String username;
        private String password;
    }
}
