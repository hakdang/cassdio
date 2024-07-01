package kr.hakdang.cadio.core.domain.cluster.keyspace.table;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import com.datastax.oss.protocol.internal.util.Bytes;
import io.micrometer.common.util.StringUtils;
import kr.hakdang.cadio.core.domain.cluster.BaseClusterCommander;
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
public class ClusterTableCommander extends BaseClusterCommander {

    public ClusterTablePureSelectResult pureSelect(ClusterTablePureSelectArgs args) {
        try (CqlSession session = makeSession()) {
            SimpleStatement statement = QueryBuilder
                .selectFrom(args.getKeyspace(), args.getTable())
                .all()
                .build()
                .setPageSize(args.getLimit());

            if (StringUtils.isNotBlank(args.getNextPageState())) {
                statement = statement.setPagingState(Bytes.fromHexString(args.getNextPageState()));
            }

            ResultSet rs = session.execute(statement);
            //rs.wasApplied()

            return null;

        } catch (Exception e) {
            log.error("error : {}", e.getMessage(), e);
            throw e;
        }
    }
}
