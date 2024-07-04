package kr.hakdang.cadio.core.domain.cluster.keyspace;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
        this.keyspaceList = keyspaceList.stream()
            .sorted(Comparator.comparing(KeyspaceResult::isSystemKeyspace).reversed()
                .thenComparing(KeyspaceResult::getKeyspaceName))
            .collect(Collectors.toList());
    }

}
