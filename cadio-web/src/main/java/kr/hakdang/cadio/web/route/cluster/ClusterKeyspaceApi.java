package kr.hakdang.cadio.web.route.cluster;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

/**
 * ClusterKeyspaceApi
 *
 * @author akageun
 * @since 2024-06-30
 */
@RestController
@RequestMapping("/api/cassandra/cluster/{clusterId}")
public class ClusterKeyspaceApi {

    @GetMapping("/keyspace")
    public Map<String, Object> keyspaceList(
        @PathVariable String clusterId
    ) {
        return Collections.emptyMap();
    }

    @GetMapping("/keyspace/{keyspace}")
    public Map<String, Object> keyspaceDetail(
        @PathVariable String clusterId,
        @PathVariable String keyspace
    ) {
        return Collections.emptyMap();
    }
}
