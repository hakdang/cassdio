package kr.hakdang.cadio.core.domain.cluster.keyspace.table;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ColumnDefinition;
import com.datastax.oss.driver.api.core.cql.ColumnDefinitions;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import com.datastax.oss.protocol.internal.util.Bytes;
import io.micrometer.common.util.StringUtils;
import kr.hakdang.cadio.core.domain.cluster.BaseClusterCommander;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

    public ClusterTablePureSelectResult pureSelect(CqlSession session, ClusterTablePureSelectArgs args) {
        SimpleStatement statement = QueryBuilder.selectFrom(args.getKeyspace(), args.getTable()).all().build().setPageSize(args.getLimit()).setTimeout(Duration.ofSeconds(3))  // 3s timeout
            .setPagingState(StringUtils.isNotBlank(args.getCursor()) ? Bytes.fromHexString(args.getCursor()) : null);

        ResultSet resultSet = session.execute(statement);
        Iterator<Row> page1Iter = resultSet.iterator();
        ColumnDefinitions definitions = resultSet.getColumnDefinitions();

        List<Map<String, Object>> rows = new ArrayList<>();
        while (0 < resultSet.getAvailableWithoutFetching()) {
            rows.add(convertMap(definitions, page1Iter.next()));
        }

        String previewCursor = args.getCursor();
        String nextCursor = "";
        ByteBuffer pagingState = resultSet.getExecutionInfo().getPagingState();
        if (pagingState != null) {
            nextCursor = Bytes.toHexString(pagingState);
        }

        List<String> columnNames = new ArrayList<>();
        for (ColumnDefinition definition : definitions) {
            columnNames.add(definition.getName().asCql(true));
        }

        return ClusterTablePureSelectResult.builder()
            .wasApplied(resultSet.wasApplied())
            .columnNames(columnNames)
            .rows(rows)
            .nextToken(nextCursor)
            .previewToken(previewCursor)
            .build();
    }

    //TODO : 중복 제거 필요
    private Map<String, Object> convertMap(ColumnDefinitions definitions, Row row) {
        Map<String, Object> result = new HashMap<>();

        for (int i = 0; i < definitions.size(); i++) {
            ColumnDefinition definition = definitions.get(i);
            String name = definition.getName().asCql(true);
            TypeCodec<Object> codec = row.codecRegistry().codecFor(definition.getType());
            Object value = codec.decode(row.getBytesUnsafe(i), row.protocolVersion());

            result.put(name, value);
        }

        return result;
    }
}
