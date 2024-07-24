package kr.hakdang.cassdio.core.domain.cluster.keyspace;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.internal.core.metadata.schema.queries.KeyspaceFilter;
import io.micrometer.common.util.StringUtils;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * KeyspaceDTO
 *
 * @author akageun
 * @since 2024-07-09
 */
public class KeyspaceDTO {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class KeyspaceInfo {
        private String keyspaceName;
        private boolean durableWrites;
        private Map<String, String> replication;
        private boolean isSystemKeyspace;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class KeyspaceListResult {
        private List<KeyspaceInfo> keyspaceInfoList;
        //private List<>
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ClusterKeyspaceDescribeArgs {
        private String keyspace;
        private boolean withChildren = false;
        private boolean pretty = true;

        @Builder
        public ClusterKeyspaceDescribeArgs(String keyspace, boolean withChildren, boolean pretty) {
            if (StringUtils.isBlank(keyspace)) {
                throw new IllegalArgumentException("keyspaceName is null or empty");
            }
            this.keyspace = keyspace;
            this.withChildren = withChildren;
            this.pretty = pretty;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ClusterKeyspaceListResult {

        private boolean wasApplied;
        private List<KeyspaceResult> keyspaceList;

        @Builder
        public ClusterKeyspaceListResult(boolean wasApplied, List<KeyspaceResult> keyspaceList) {
            this.wasApplied = wasApplied;
            this.keyspaceList = keyspaceList;
        }

    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class KeyspaceNameResult {

        private String keyspaceName;
        private boolean systemKeyspace;

        @Builder
        public KeyspaceNameResult(String keyspaceName, boolean systemKeyspace) {
            this.keyspaceName = keyspaceName;
            this.systemKeyspace = systemKeyspace;
        }

        public static KeyspaceNameResult make(Row row, KeyspaceFilter keyspaceFilter) {
            String keyspaceName = row.getString("keyspace_name");

            return KeyspaceNameResult.builder()
                .keyspaceName(keyspaceName)
                .systemKeyspace(!keyspaceFilter.includes(keyspaceName))
                .build();
        }
    }
}
