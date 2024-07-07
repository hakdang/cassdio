package kr.hakdang.cassdio.core.domain.cluster.keyspace;

import io.micrometer.common.util.StringUtils;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ClusterKeyspaceDescribeArgs
 *
 * @author akageun
 * @since 2024-06-30
 */
@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClusterKeyspaceDescribeArgs {
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
