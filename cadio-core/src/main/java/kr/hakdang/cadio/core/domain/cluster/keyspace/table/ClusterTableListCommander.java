package kr.hakdang.cadio.core.domain.cluster.keyspace.table;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.protocol.internal.util.Bytes;
import io.micrometer.common.util.StringUtils;
import kr.hakdang.cadio.core.domain.cluster.BaseClusterCommander;
import kr.hakdang.cadio.core.domain.cluster.keyspace.table.column.Column;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.bindMarker;

/**
 * ClusterTableListCommander
 *
 * @author seungh0
 * @since 2024-07-01
 */
@Slf4j
@Service
public class ClusterTableListCommander extends BaseClusterCommander {

    public ClusterTableListResult listTables(ClusterTableListArgs args) {
        try (CqlSession session = makeSession()) {
            SimpleStatement statement = QueryBuilder
                .selectFrom("system_schema", "tables")
                .all()
                .whereColumn("keyspace_name").isEqualTo(bindMarker())
                .limit(args.getLimit())
                .build(args.getKeyspace())
                .setPagingState(StringUtils.isBlank(args.getNextPageState()) ? null : Bytes.fromHexString(args.getNextPageState()));

            ResultSet rs = session.execute(statement);
            if (!rs.wasApplied()) {
                throw new IllegalStateException(String.format("failed to load keyspace(%s)'s tables.", args.getKeyspace()));
            }

            List<ClusterTable> tables = rs.all().stream()
                .map(ClusterTable::from)
                .collect(Collectors.toList());

            Map<ClusterTable, List<Column>> tableColumns = getTableColumns(session, tables);

            return ClusterTableListResult.of(tableColumns, rs.getExecutionInfo().getPagingState());
        } catch (Exception e) {
            log.error("error : {}", e.getMessage(), e);
            throw e;
        }
    }

    private Map<ClusterTable, List<Column>> getTableColumns(CqlSession session, List<ClusterTable> tables) {
        Map<ClusterTable, List<Column>> result = new HashMap<>();
        for (ClusterTable table : tables) {
            result.put(table, Collections.emptyList());
        }
        return result;
    }

}
