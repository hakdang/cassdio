package kr.hakdang.cassdio.web.route.cluster.keyspace.table;

import com.datastax.oss.driver.api.core.CqlSession;
import jakarta.validation.Valid;
import kr.hakdang.cassdio.core.domain.cluster.TempClusterConnector;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.ClusterTable;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.ClusterTableArgs;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.ClusterTableCommander;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.ClusterTableGetResult2;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.column.ClusterTableColumnCommander;
import kr.hakdang.cassdio.web.common.dto.request.CursorRequest;
import kr.hakdang.cassdio.web.common.dto.response.ApiResponse;
import kr.hakdang.cassdio.web.common.dto.response.ItemListWithCursorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * ClusterTableApi
 *
 * @author seungh0
 * @since 2024-07-01
 */
@RestController
@RequestMapping("/api/cassandra/cluster/{clusterId}/keyspace/{keyspace}")
public class ClusterTableApi {

    private final ClusterTableReader clusterTableReader;
    private final TempClusterConnector tempClusterConnector;
    private final ClusterTableCommander clusterTableCommander;
    private final ClusterTableColumnCommander clusterTableColumnCommander;

    public ClusterTableApi(
        ClusterTableReader clusterTableReader,
        TempClusterConnector tempClusterConnector,
        ClusterTableCommander clusterTableCommander,
        ClusterTableColumnCommander clusterTableColumnCommander
    ) {
        this.clusterTableReader = clusterTableReader;
        this.tempClusterConnector = tempClusterConnector;
        this.clusterTableCommander = clusterTableCommander;
        this.clusterTableColumnCommander = clusterTableColumnCommander;
    }

    @GetMapping("/table")
    public ApiResponse<ItemListWithCursorResponse<ClusterTable, String>> listTables(
        @PathVariable String clusterId,
        @PathVariable String keyspace,
        @Valid CursorRequest cursorRequest
    ) {
        ItemListWithCursorResponse<ClusterTable, String> tables = clusterTableReader.listTables(clusterId, keyspace, cursorRequest);
        return ApiResponse.ok(tables);
    }

    @GetMapping("/table/{table}")
    public ApiResponse<Map<String, Object>> getTable(
        @PathVariable String clusterId,
        @PathVariable String keyspace,
        @PathVariable String table
    ) {
        Map<String, Object> result = new HashMap<>();

        try (CqlSession session = tempClusterConnector.makeSession(clusterId, keyspace)) {
            result.put("detail", clusterTableCommander.tableDetail(session, ClusterTableArgs.ClusterTableGetArgs.builder()
                .keyspace(keyspace)
                .table(table)
                .build()));

            result.put("describe", clusterTableCommander.tableDescribe(session, keyspace, table));
            result.put("columnList", clusterTableColumnCommander.columnList(session, keyspace, table));
        }

        return ApiResponse.ok(result);
    }

    @GetMapping("/table/{table}/column")
    public ApiResponse<Map<String, Object>> getTableColumn(
        @PathVariable String clusterId,
        @PathVariable String keyspace,
        @PathVariable String table
    ) {
        Map<String, Object> result = new HashMap<>();

        try (CqlSession session = tempClusterConnector.makeSession(clusterId, keyspace)) {
            result.put("columnList", clusterTableColumnCommander.columnList(session, keyspace, table));
        }

        return ApiResponse.ok(result);
    }

}
