package kr.hakdang.cadio.core.domain.cluster.keyspace;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.metadata.schema.KeyspaceMetadata;
import kr.hakdang.cadio.core.domain.cluster.ClusterArgs;
import kr.hakdang.cadio.core.domain.cluster.ClusterCommandPreExecuteResult;
import kr.hakdang.cadio.core.domain.cluster.ClusterCommander;
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
public class ClusterKeyspaceDescribeCommander
    extends ClusterCommander<ClusterKeyspaceDescribeArgs, ClusterKeyspaceDescribeResult> {

    @Override
    protected void doArgValid(ClusterArgs cluster, ClusterKeyspaceDescribeArgs args) {

    }

    @Override
    protected ClusterKeyspaceDescribeResult doExecute(ClusterArgs cluster, ClusterKeyspaceDescribeArgs args, ClusterCommandPreExecuteResult preResult) {
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
