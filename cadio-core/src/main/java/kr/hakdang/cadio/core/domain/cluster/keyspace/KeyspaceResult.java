package kr.hakdang.cadio.core.domain.cluster.keyspace;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * KeyspaceResult
 *
 * @author akageun
 * @since 2024-07-01
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KeyspaceResult {

    private String keyspaceName;
    private boolean durableWrites;
    private Map<String, String> replication;
    private boolean isSystemKeyspace;

    @Builder
    public KeyspaceResult(String keyspaceName, boolean durableWrites, Map<String, String> replication) {
        this.keyspaceName = keyspaceName;
        this.durableWrites = durableWrites;
        this.replication = replication;
        this.isSystemKeyspace = CassandraSystemKeyspace.isSystemKeyspace(keyspaceName);
    }
}
