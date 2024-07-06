package kr.hakdang.cassdio.core.domain.cluster.keyspace;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * ClusterKeyspaceDescribeResult
 *
 * @author akageun
 * @since 2024-06-30
 */
@ToString
@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClusterKeyspaceDescribeResult {

    private String describe;

    @Builder
    public ClusterKeyspaceDescribeResult(String describe) {
        this.describe = describe;
    }
}