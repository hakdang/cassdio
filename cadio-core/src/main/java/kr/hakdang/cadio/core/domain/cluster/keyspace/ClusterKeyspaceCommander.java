package kr.hakdang.cadio.core.domain.cluster.keyspace;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.metadata.schema.KeyspaceMetadata;
import kr.hakdang.cadio.core.domain.cluster.BaseClusterCommander;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * ClusterKeyspaceDescribeCommander
 *
 * @author akageun
 * @since 2024-06-30
 */
@Slf4j
@Service
public class ClusterKeyspaceCommander extends BaseClusterCommander {


    public ClusterKeyspaceDescribeResult describe(ClusterKeyspaceDescribeArgs args) {
        try (CqlSession session = makeSession()) {

            KeyspaceMetadata keyspaceMetadata = session.getMetadata().getKeyspace(args.getKeyspace())
                .orElseThrow(() -> new RuntimeException("not found keyspace"));

            String describe = args.isWithChildren() ?
                keyspaceMetadata.describeWithChildren(args.isPretty()) :
                keyspaceMetadata.describe(args.isPretty());

            return ClusterKeyspaceDescribeResult.builder()
                .describe(describe)
                .build();

        } catch (Exception e) {
            log.error("error : {}", e.getMessage(), e);
            throw e;
        }
    }
}
