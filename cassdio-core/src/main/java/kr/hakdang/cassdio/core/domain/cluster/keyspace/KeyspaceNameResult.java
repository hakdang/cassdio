package kr.hakdang.cassdio.core.domain.cluster.keyspace;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.internal.core.metadata.schema.queries.KeyspaceFilter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * KeyspaceNameResult
 *
 * @author akageun
 * @since 2024-07-01
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KeyspaceNameResult {

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
