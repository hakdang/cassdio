package kr.hakdang.cassdio.core.domain.cluster.keyspace;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
