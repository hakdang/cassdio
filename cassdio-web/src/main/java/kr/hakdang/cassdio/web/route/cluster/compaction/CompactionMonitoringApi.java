package kr.hakdang.cassdio.web.route.cluster.compaction;

import kr.hakdang.cassdio.core.domain.cluster.compaction.CompactionHistoryListCommander;
import kr.hakdang.cassdio.core.domain.cluster.compaction.CompactionHistoryListResult;
import kr.hakdang.cassdio.web.common.dto.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * CompactionMonitoringApi
 *
 * @author seungh0
 * @since 2024-07-25
 */
@RequestMapping("/api/cassandra/cluster/{clusterId}")
@RestController
public class CompactionMonitoringApi {
    private final CompactionHistoryListCommander compactionHistoryListCommander;

    public CompactionMonitoringApi(
        CompactionHistoryListCommander compactionHistoryListCommander
    ) {
        this.compactionHistoryListCommander = compactionHistoryListCommander;
    }

    @GetMapping("/compaction-history")
    public ApiResponse<CompactionHistoryListResult> getCompactionHistories(
        @PathVariable String clusterId,
        @RequestParam(required = false) String keyspace
    ) {
        CompactionHistoryListResult result = compactionHistoryListCommander.getCompactionHistories(clusterId, keyspace);
        return ApiResponse.ok(result);
    }

}
