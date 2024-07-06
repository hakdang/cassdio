package kr.hakdang.cassdio.core.domain.cluster.keyspace.table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * ClusterTableGetArgs
 *
 * @author seungh0
 * @since 2024-07-02
 */
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClusterTableGetArgs {

    private String keyspace;
    private String table;

    @Builder
    private ClusterTableGetArgs(String keyspace, String table) {
        this.keyspace = keyspace;
        this.table = table;
    }

}
