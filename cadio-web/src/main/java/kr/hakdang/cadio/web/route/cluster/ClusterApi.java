package kr.hakdang.cadio.web.route.cluster;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

/**
 * ClusterApi
 *
 * @author akageun
 * @since 2024-06-30
 */
@RestController
@RequestMapping("/api/cassandra/cluster")
public class ClusterApi {

    @GetMapping("")
    public Map<String, Object> getCassandraClusterList() {
        return Collections.emptyMap();
    }

    @GetMapping("/{clusterId}")
    public Map<String, Object> getCassandraClusterDetail(
        @PathVariable String clusterId
    ) {
        return Collections.emptyMap();
    }
}
