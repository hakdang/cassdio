package kr.hakdang.cassdio.core.domain.cluster.keyspace.table;

import io.micrometer.common.util.StringUtils;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ClusterDescTablesArgs
 *
 * @author akageun
 * @since 2024-07-05
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClusterTableListArgs {
    private String keyspace;
    private int pageSize;

    private String cursor;

    @Builder
    public ClusterTableListArgs(String keyspace, int pageSize, String cursor) {
        if (StringUtils.isBlank(keyspace)) {
            throw new IllegalArgumentException("keyspace can't be null or empty");
        }

        if (pageSize > 100) {
            throw new IllegalArgumentException("pageSize can't be greater than 100");
        }

        this.keyspace = keyspace;
        this.pageSize = pageSize;
        this.cursor = cursor;
    }
}
