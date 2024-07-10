package kr.hakdang.cassdio.core.domain.cluster.keyspace;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.internal.core.metadata.schema.queries.KeyspaceFilter;
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
    private boolean systemKeyspace;

    @Builder
    public KeyspaceResult(String keyspaceName, boolean durableWrites, Map<String, String> replication, boolean systemKeyspace) {
        this.keyspaceName = keyspaceName;
        this.durableWrites = durableWrites;
        this.replication = replication;
        this.systemKeyspace = systemKeyspace;
    }

    public static KeyspaceResult make(Row row, KeyspaceFilter keyspaceFilter) {
        return KeyspaceResult.builder()
            .keyspaceName(row.getString("keyspace_name"))
            .durableWrites(row.getBoolean("durable_writes"))
            .replication(row.getMap("replication", String.class, String.class))
            .systemKeyspace(!keyspaceFilter.includes(row.getString("keyspace_name")))
            .build();
    }
}
