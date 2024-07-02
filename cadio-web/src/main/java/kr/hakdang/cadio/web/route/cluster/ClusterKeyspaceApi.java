package kr.hakdang.cadio.web.route.cluster;

import com.datastax.oss.driver.api.core.CqlSession;
import kr.hakdang.cadio.core.domain.cluster.keyspace.ClusterKeyspaceCommander;
import kr.hakdang.cadio.core.domain.cluster.keyspace.ClusterKeyspaceDescribeArgs;
import kr.hakdang.cadio.web.route.BaseSample;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
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
public class ClusterKeyspaceApi extends BaseSample {

    @Autowired
    private ClusterKeyspaceCommander clusterKeyspaceCommander;


    @GetMapping("/keyspace")
    public Map<String, Object> keyspaceList(
        @PathVariable String clusterId
    ) {
        Map<String, Object> result = new HashMap<>();
        try (CqlSession session = makeSession()) { //TODO : interface 작업할 때 facade layer 로 변경 예정
            result.put("result", clusterKeyspaceCommander.keyspaceList(session));

        } catch (Exception e) {
            log.error("error : {}", e.getMessage(), e);
            throw e;
        }

        return result;
    }

    @GetMapping("/keyspace/{keyspace}")
    public Map<String, Object> keyspaceDetail(
        @PathVariable String clusterId,
        @PathVariable String keyspace
    ) {
        Map<String, Object> result = new HashMap<>();
        try (CqlSession session = makeSession()) { //TODO : interface 작업할 때 facade layer 로 변경 예정
            result.put("describe", clusterKeyspaceCommander.describe(session, ClusterKeyspaceDescribeArgs.builder()
                .keyspace(keyspace)
                .withChildren(false)
                .pretty(true)
                .build()));

        } catch (Exception e) {
            log.error("error : {}", e.getMessage(), e);
            throw e;
        }

        return result;
    }
}
