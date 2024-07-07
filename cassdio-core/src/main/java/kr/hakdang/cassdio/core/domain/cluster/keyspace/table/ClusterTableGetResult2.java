package kr.hakdang.cassdio.core.domain.cluster.keyspace.table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;

/**
 * ClusterTableGetResult
 *
 * @author seungh0
 * @since 2024-07-02
 */
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClusterTableGetResult2 {

    private Map<String, Object> table;
    private Map<String, Object> describe;

    @Builder
    private ClusterTableGetResult2(Map<String, Object> table, Map<String, Object> describe) {
        this.table = table;
        this.describe = describe;
    }

}
