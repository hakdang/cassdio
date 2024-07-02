package kr.hakdang.cadio.web.route.cluster;

import com.datastax.oss.driver.api.core.CqlSession;
import kr.hakdang.cadio.web.route.BaseSample;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

    @GetMapping("")
    public Map<String, Object> getCassandraClusterList() {
        return Collections.emptyMap();
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
        return Collections.emptyMap();
    }
}
