package kr.hakdang.cassdio.core.domain.cluster.query;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ColumnDefinitions;
import com.datastax.oss.driver.api.core.cql.QueryTrace;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.cql.TraceEvent;
import com.datastax.oss.driver.api.core.type.codec.registry.CodecRegistry;
import com.datastax.oss.protocol.internal.util.Bytes;
import io.micrometer.common.util.StringUtils;
import kr.hakdang.cassdio.core.domain.cluster.BaseClusterCommander;
import kr.hakdang.cassdio.core.domain.cluster.ClusterUtils;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.CassdioColumnDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.ArrayList;
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
public class ClusterQueryCommander extends BaseClusterCommander {

    public QueryDTO.ClusterQueryCommanderResult execute(CqlSession session, QueryDTO.ClusterQueryCommanderArgs args) {
        SimpleStatement statement = SimpleStatement.builder(args.getQuery())
            .setPageSize(args.getPageSize())                    // 10 per pages
            .setTimeout(Duration.ofSeconds(args.getTimeoutSeconds()))  // 3s timeout
            .setPagingState(StringUtils.isNotBlank(args.getCursor()) ? Bytes.fromHexString(args.getCursor()) : null)
            .setTracing(args.isTrace())
            .build();
        //.setConsistencyLevel(ConsistencyLevel.ONE);

        ResultSet resultSet = session.execute(statement);
        ColumnDefinitions definitions = resultSet.getColumnDefinitions();

        ByteBuffer pagingStateAsBytes = resultSet.getExecutionInfo().getPagingState();

        QueryDTO.ClusterQueryCommanderResult.ClusterQueryCommanderResultBuilder builder =
            QueryDTO.ClusterQueryCommanderResult.builder()
                .wasApplied(resultSet.wasApplied())
                .rowHeader(CassdioColumnDefinition.makes(definitions))
                .rows(convertRows(session, resultSet))
                .nextCursor(pagingStateAsBytes != null ? Bytes.toHexString(pagingStateAsBytes) : null);

        if (args.isTrace()) {
            QueryTrace queryTrace = resultSet.getExecutionInfo().getQueryTrace();

            List<QueryDTO.CassdioQueryTraceEvent> events = new ArrayList<>();
            for (TraceEvent event : queryTrace.getEvents()) {
                events.add(QueryDTO.CassdioQueryTraceEvent.builder()
                    .activity(event.getActivity())
                    .timestamp(event.getTimestamp())
                    .sourceAddress(event.getSourceAddress())
                    .sourceElapsedMicros(event.getSourceElapsedMicros())
                    .threadName(event.getThreadName())
                    .build());
            }

            QueryDTO.CassdioQueryTrace cassdioQueryTrace = QueryDTO.CassdioQueryTrace.builder()
                .tracingId(queryTrace.getTracingId().toString())
                .requestType(queryTrace.getRequestType())
                .coordinatorAddress(queryTrace.getCoordinatorAddress())
                .parameters(queryTrace.getParameters())
                .startedAt(queryTrace.getStartedAt())
                .events(events)
                .build();

            builder.queryTrace(cassdioQueryTrace);
        }

        return
            builder.build();
    }
}
