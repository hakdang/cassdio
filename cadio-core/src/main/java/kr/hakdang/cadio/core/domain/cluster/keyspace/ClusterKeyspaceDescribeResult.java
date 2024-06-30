package kr.hakdang.cadio.core.domain.cluster.keyspace;

import kr.hakdang.cadio.core.domain.cluster.ClusterCommandResult;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ClusterKeyspaceDescribeResult
 *
 * @author akageun
 * @since 2024-06-30
 */
@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClusterKeyspaceDescribeResult implements ClusterCommandResult {

    private String describe;

    @Builder
    public ClusterKeyspaceDescribeResult(String describe) {
        this.describe = describe;
    }
}
