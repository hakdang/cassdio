package kr.hakdang.cassdio.core.domain.cluster.compaction;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * ClusterTable
 *
 * @author seungh0
 * @since 2024-07-01
 */
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompactionHistoryListResult {

    private List<CompactionHistory> histories;

    @Builder
    public CompactionHistoryListResult(List<CompactionHistory> histories) {
        this.histories = histories;
    }

}
