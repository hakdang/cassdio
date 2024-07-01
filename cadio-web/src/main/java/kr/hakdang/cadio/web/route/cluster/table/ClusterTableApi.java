package kr.hakdang.cadio.web.route.cluster.table;

import jakarta.validation.Valid;
import kr.hakdang.cadio.core.domain.cluster.keyspace.table.ClusterTable;
import kr.hakdang.cadio.core.domain.cluster.keyspace.table.ClusterTableListArgs;
import kr.hakdang.cadio.core.domain.cluster.keyspace.table.ClusterTableListCommander;
import kr.hakdang.cadio.core.domain.cluster.keyspace.table.ClusterTableListResult;
import kr.hakdang.cadio.web.common.dto.request.CursorRequest;
import kr.hakdang.cadio.web.common.dto.response.ApiResponse;
import kr.hakdang.cadio.web.common.dto.response.CursorResponse;
import kr.hakdang.cadio.web.common.dto.response.ItemListWithCursorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClusterTableApi
 *
 * @author seungh0
 * @since 2024-07-01
 */
@RestController
@RequestMapping("/api/cassandra/clusters/{clusterId}/keyspaces/{keyspace}")
public class ClusterTableApi {

    private final ClusterTableListCommander clusterTableListCommander;

    public ClusterTableApi(
        ClusterTableListCommander clusterTableListCommander
    ) {
        this.clusterTableListCommander = clusterTableListCommander;
    }

    @GetMapping("/tables")
    public ApiResponse<ItemListWithCursorResponse<ClusterTable, String>> listTables(
        @PathVariable String clusterId,
        @PathVariable String keyspace,
        @Valid CursorRequest cursorRequest
    ) {
        ClusterTableListResult result = clusterTableListCommander.listTables(ClusterTableListArgs.builder()
            .keyspace(keyspace)
            .limit(cursorRequest.getSize())
            .nextPageState(cursorRequest.getCursor())
            .build());
        return ApiResponse.ok(ItemListWithCursorResponse.of(result.getTables(), CursorResponse.withNext(result.getNextPageState())));
    }

}
