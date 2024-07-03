package kr.hakdang.cadio.core.domain.cluster;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ColumnDefinition;
import com.datastax.oss.driver.api.core.cql.ColumnDefinitions;
import com.datastax.oss.driver.api.core.cql.QueryTrace;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.cql.TraceEvent;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.protocol.internal.util.Bytes;
import io.micrometer.common.util.StringUtils;
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
 * ClusterQueryCommander
 *
 * @author akageun
 * @since 2024-07-03
 */
@Slf4j
@Service
public class ClusterQueryCommander {

    public ClusterQueryCommanderResult execute(CqlSession session, ClusterQueryCommanderArgs args) {
        SimpleStatement statement = SimpleStatement.builder(args.getQuery())
            .setPageSize(args.getPageSize())                    // 10 per pages
            .setTimeout(Duration.ofSeconds(args.getTimeoutSeconds()))  // 3s timeout
            .setPagingState(StringUtils.isNotBlank(args.getCursor()) ? Bytes.fromHexString(args.getCursor()) : null)
            .setTracing(args.isTrace())
            .build();
        //.setConsistencyLevel(ConsistencyLevel.ONE);

        ResultSet resultSet = session.execute(statement);

        ColumnDefinitions definitions = resultSet.getColumnDefinitions();

        Iterator<Row> page1Iter = resultSet.iterator();

        List<Map<String, Object>> rows = new ArrayList<>();
        while (0 < resultSet.getAvailableWithoutFetching()) {
            rows.add(convertMap(definitions, page1Iter.next()));
        }

        ByteBuffer pagingStateAsBytes = resultSet.getExecutionInfo().getPagingState();

        List<String> columnNames = new ArrayList<>();
        for (ColumnDefinition definition : definitions) {
            //TODO : 컬럼 정보 이름 외에도 추가하기
            columnNames.add(definition.getName().asCql(true));
        }

        if (args.isTrace()) {
            QueryTrace queryTrace = resultSet.getExecutionInfo().getQueryTrace();
            log.info("query Trace : {}", queryTrace.getTracingId());
            for (TraceEvent event : queryTrace.getEvents()) {
                log.info("event : {}", event);
                //TODO : 추적값 담기
            }
        }

        return ClusterQueryCommanderResult.builder()
            .wasApplied(resultSet.wasApplied())
            .columnNames(columnNames)
            .rows(rows)
            .nextCursor(pagingStateAsBytes != null ? Bytes.toHexString(pagingStateAsBytes) : null)
            .build();
    }

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