package kr.hakdang.cassdio.core.domain.cluster.query;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.Version;
import com.datastax.oss.driver.api.core.cql.ColumnDefinitions;
import com.datastax.oss.driver.api.core.cql.QueryTrace;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.cql.SimpleStatementBuilder;
import com.datastax.oss.driver.api.core.cql.TraceEvent;
import com.datastax.oss.protocol.internal.util.Bytes;
import io.micrometer.common.util.StringUtils;
import kr.hakdang.cassdio.common.error.NotSupportedCassandraVersionException;
import kr.hakdang.cassdio.core.domain.cluster.BaseClusterCommander;
import kr.hakdang.cassdio.core.domain.cluster.ClusterVersionEvaluator;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.CassdioColumnDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * ClusterQueryCommander
 *
 * @author akageun
 * @since 2024-07-03
 */
@Slf4j
@Service
public class ClusterQueryCommander extends BaseClusterCommander {

    private final ClusterVersionEvaluator clusterVersionEvaluator;

    public ClusterQueryCommander(
        ClusterVersionEvaluator clusterVersionEvaluator
    ) {
        this.clusterVersionEvaluator = clusterVersionEvaluator;
    }

    public boolean useKeyspaceQueryCommandNotSupport(String clusterId) {
        return clusterVersionEvaluator.isLessThan(clusterId, Version.V4_0_0);
    }

    public boolean useKeyspaceQueryCommandNotSupportWithSession(CqlSession session) {
        return clusterVersionEvaluator.isLessThan(session, Version.V4_0_0);
    }

    public QueryDTO.ClusterQueryCommanderResult execute(
        String clusterId,
        QueryDTO.ClusterQueryCommanderArgs args
    ) {
        CqlSession session = cqlSessionFactory.get(clusterId);
        if (useKeyspaceQueryCommandNotSupportWithSession(session)) {
            if (StringUtils.isNotBlank(args.getKeyspace())) {
                throw new NotSupportedCassandraVersionException("It is available in Cassandra version 4.0 and later");
            }
        }

        SimpleStatementBuilder simpleBuilder = SimpleStatement.builder(args.getQuery())
            .setPageSize(args.getPageSize())                    // 10 per pages
            .setTimeout(Duration.ofSeconds(args.getTimeoutSeconds()))  // 3s timeout
            .setPagingState(StringUtils.isNotBlank(args.getCursor()) ? Bytes.fromHexString(args.getCursor()) : null)
            .setConsistencyLevel(args.getConsistencyLevel())
            .setTracing(args.isTrace());

        if (StringUtils.isNotBlank(args.getKeyspace())) {
            simpleBuilder.setKeyspace(args.getKeyspace());
        }

        SimpleStatement statement = simpleBuilder.build();

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

        return builder.build();
    }
}
