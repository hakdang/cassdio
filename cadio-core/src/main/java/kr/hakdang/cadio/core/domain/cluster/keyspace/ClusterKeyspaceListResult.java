package kr.hakdang.cadio.core.domain.cluster.keyspace;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * ClusterKeyspaceListResult
 *
 * @author akageun
 * @since 2024-07-01
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClusterKeyspaceListResult {

    private boolean wasApplied;
    private List<KeyspaceResult> keyspaceList;

    @Builder
    public ClusterKeyspaceListResult(boolean wasApplied, List<KeyspaceResult> keyspaceList) {
        this.wasApplied = wasApplied;
        this.keyspaceList = keyspaceList;
    }
}
