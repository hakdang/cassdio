package kr.hakdang.cassdio.web.route.cluster.keyspace.table;

import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.TableDTO.ClusterTableRowArgs;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ClusterTablePureSelectRequest
 *
 * @author akageun
 * @since 2024-07-03
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClusterTableRowRequest {
    private String cursor;
    private int pageSize;
    private int timeoutSeconds;

    @Builder
    public ClusterTableRowRequest(String cursor, int pageSize, int timeoutSeconds) {
        this.cursor = cursor;
        this.pageSize = pageSize;
        if (timeoutSeconds <= 0 || timeoutSeconds > 30) {
            this.timeoutSeconds = 3;
        } else {
            this.timeoutSeconds = timeoutSeconds;
        }

    }

    public ClusterTableRowArgs makeArgs(String keyspace, String table) {
        return ClusterTableRowArgs.builder()
            .keyspace(keyspace)
            .table(table)
            .cursor(cursor)
            .pageSize(pageSize)
            .timeoutSeconds(timeoutSeconds)
            .build();
    }
}
