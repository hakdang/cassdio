package kr.hakdang.cassdio.core.domain.cluster.query;

import com.datastax.oss.driver.api.core.cql.QueryTrace;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.CassdioColumnDefinition;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

/**
 * QueryDTO
 *
 * @author akageun
 * @since 2024-07-25
 */
public class QueryDTO {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ClusterQueryCommanderArgs {
        private String keyspace;
        private String query;
        private String cursor;
        private int pageSize;
        private int timeoutSeconds;
        private boolean trace = false;


        @Builder
        public ClusterQueryCommanderArgs(String keyspace, String query, String cursor, Integer pageSize, Integer timeoutSeconds, boolean trace) {
            if (pageSize == null || pageSize <= 0) {
                pageSize = 50;
            }

            if (pageSize > 500) {
                throw new RuntimeException("pageSize 500 over");
            }

            if (timeoutSeconds == null || timeoutSeconds <= 0) {
                timeoutSeconds = 3; //default 3
            }

            if (timeoutSeconds > 60) {
                throw new RuntimeException("timeout 60 over");
            }

            this.keyspace = keyspace;
            this.query = query;
            this.cursor = cursor;
            this.pageSize = pageSize;
            this.timeoutSeconds = timeoutSeconds;
            this.trace = trace;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ClusterQueryCommanderResult {
        private boolean wasApplied;
        private List<CassdioColumnDefinition> rowHeader;
        private List<Map<String, Object>> rows;
        private String previousCursor;
        private String nextCursor;
        private CassdioQueryTrace queryTrace;

        @Builder
        public ClusterQueryCommanderResult(
            boolean wasApplied,
            List<CassdioColumnDefinition> rowHeader,
            List<Map<String, Object>> rows,
            String previousCursor,
            String nextCursor,
            CassdioQueryTrace queryTrace
        ) {
            this.wasApplied = wasApplied;
            this.rowHeader = rowHeader;
            this.rows = rows;
            this.previousCursor = previousCursor;
            this.nextCursor = nextCursor;
            this.queryTrace = queryTrace;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CassdioQueryTrace {
        private String tracingId;
        private String requestType;
        private InetSocketAddress coordinatorAddress;
        private Map<String, String> parameters;
        private long startedAt;
        private List<CassdioQueryTraceEvent> events;

        @Builder
        public CassdioQueryTrace(String tracingId, String requestType, InetSocketAddress coordinatorAddress, Map<String, String> parameters, long startedAt, List<CassdioQueryTraceEvent> events) {
            this.tracingId = tracingId;
            this.requestType = requestType;
            this.coordinatorAddress = coordinatorAddress;
            this.parameters = parameters;
            this.startedAt = startedAt;
            this.events = events;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CassdioQueryTraceEvent {
        private String activity;
        private long timestamp;
        private InetSocketAddress sourceAddress;
        private int sourceElapsedMicros;
        private String threadName;

        @Builder
        public CassdioQueryTraceEvent(String activity, long timestamp, InetSocketAddress sourceAddress, int sourceElapsedMicros, String threadName) {
            this.activity = activity;
            this.timestamp = timestamp;
            this.sourceAddress = sourceAddress;
            this.sourceElapsedMicros = sourceElapsedMicros;
            this.threadName = threadName;
        }
    }
}
