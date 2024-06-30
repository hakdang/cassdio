package kr.hakdang.cadio.core.domain.cluster.keyspace.table;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.metadata.schema.KeyspaceMetadata;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import com.datastax.oss.driver.api.querybuilder.select.SelectFrom;
import kr.hakdang.cadio.core.domain.cluster.ClusterArgs;
import kr.hakdang.cadio.core.domain.cluster.ClusterCommandPreExecuteResult;
import kr.hakdang.cadio.core.domain.cluster.ClusterCommander;
import kr.hakdang.cadio.core.domain.cluster.keyspace.ClusterKeyspaceDescribeResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * ClusterTablePureSelectCommander
 * - 테이블 단순 조회 용
 *
 * @author akageun
 * @since 2024-06-30
 */
@Slf4j
@Service
public class ClusterTablePureSelectCommander
    extends ClusterCommander<ClusterTablePureSelectArgs, ClusterTablePureSelectResult> {

    @Override
    protected void doArgValid(ClusterArgs cluster, ClusterTablePureSelectArgs args) {

    }

    @Override
    protected ClusterTablePureSelectResult doExecute(ClusterArgs cluster, ClusterTablePureSelectArgs args, ClusterCommandPreExecuteResult preResult) {
        try (CqlSession session = makeSession()) {
            Select query = QueryBuilder.selectFrom(args.getKeyspace(), args.getTable()).all().limit(50);

            SimpleStatement statement = query.build();

            ResultSet rs = session.execute(statement);
            //rs.wasApplied()

            return null;

        } catch (Exception e) {
            log.error("error : {}", e.getMessage(), e);
            throw e;
        }
    }
}
