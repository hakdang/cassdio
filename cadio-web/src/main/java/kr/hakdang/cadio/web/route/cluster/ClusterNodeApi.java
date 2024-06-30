package kr.hakdang.cadio.web.route.cluster;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

/**
 * ClusterNodeApi
 *
 * @author akageun
 * @since 2024-06-30
 */
@RestController
@RequestMapping("/api/cassandra/cluster/{clusterId}")
public class ClusterNodeApi {

    @GetMapping("/node")
    public Map<String, Object> nodeList(
        @PathVariable String clusterId
    ) {
        return Collections.emptyMap();
    }

    @GetMapping("/node/{node}")
    public Map<String, Object> nodeDetail(
        @PathVariable String clusterId,
        @PathVariable String node
    ) {
        return Collections.emptyMap();
    }


}
