package kr.hakdang.cassdio.web.route.cluster.keyspace.table;

import jakarta.validation.Valid;
import kr.hakdang.cassdio.core.domain.cluster.CqlSessionSelectResult;
import kr.hakdang.cassdio.core.domain.cluster.CqlSessionSelectResults;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.ClusterCsvProvider;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.ClusterTableCommander;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.ClusterTableGetCommander;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.ClusterTableListCommander;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.ClusterTableRowCommander;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.TableDTO;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.column.ClusterTableColumnCommander;
import kr.hakdang.cassdio.web.common.dto.request.CursorRequest;
import kr.hakdang.cassdio.web.common.dto.response.ApiResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
    private final ClusterTableCommander clusterTableCommander;
    private final ClusterTableListCommander clusterTableListCommander;
    private final ClusterTableGetCommander clusterTableGetCommander;

    private final ClusterTableColumnCommander clusterTableColumnCommander;


    public ClusterTableApi(
        ClusterTableCommander clusterTableCommander,
        ClusterTableListCommander clusterTableListCommander,
        ClusterTableGetCommander clusterTableGetCommander,
        ClusterTableColumnCommander clusterTableColumnCommander
    ) {
        this.clusterTableCommander = clusterTableCommander;
        this.clusterTableListCommander = clusterTableListCommander;
        this.clusterTableGetCommander = clusterTableGetCommander;
        this.clusterTableColumnCommander = clusterTableColumnCommander;
    }

    @GetMapping("/table")
    public ApiResponse<Map<String, Object>> listTables(
        @PathVariable String clusterId,
        @PathVariable String keyspace,
        @Valid CursorRequest cursorRequest
    ) {
        Map<String, Object> responseMap = new HashMap<>();
        TableDTO.ClusterTableListArgs args = TableDTO.ClusterTableListArgs.builder()
            .keyspace(keyspace)
            .pageSize(cursorRequest.getSize())
            .cursor(cursorRequest.getCursor())
            .build();

        CqlSessionSelectResults tableListResult = clusterTableListCommander.tableList(clusterId, args);

        responseMap.put("tableList", tableListResult);

        return ApiResponse.ok(responseMap);
    }

    @GetMapping("/table/{table}")
    public ApiResponse<Map<String, Object>> getTable(
        @PathVariable String clusterId,
        @PathVariable String keyspace,
        @PathVariable String table
    ) {
        Map<String, Object> responseMap = new HashMap<>();

        TableDTO.ClusterTableGetArgs tableGetArgs = TableDTO.ClusterTableGetArgs.builder()
            .keyspace(keyspace)
            .table(table)
            .build();

        CqlSessionSelectResult result = clusterTableGetCommander.tableDetail(clusterId, tableGetArgs);

        responseMap.put("detail", result);

        responseMap.put("describe", clusterTableCommander.tableDescribe(clusterId, keyspace, table));
        responseMap.put("columnList", clusterTableColumnCommander.columnList(clusterId, keyspace, table));

        return ApiResponse.ok(responseMap);
    }

    @GetMapping("/table/{table}/column")
    public ApiResponse<Map<String, Object>> getTableColumn(
        @PathVariable String clusterId,
        @PathVariable String keyspace,
        @PathVariable String table
    ) {
        Map<String, Object> responseMap = new HashMap<>();

        responseMap.put("columnList", clusterTableColumnCommander.columnList(clusterId, keyspace, table));

        return ApiResponse.ok(responseMap);
    }

    //권한 추가해서 ADMIN 만 동작할 수 있도록 해야함.
    @DeleteMapping("/table/{table}/drop")
    public ApiResponse<Void> tableDrop(
        @PathVariable String clusterId,
        @PathVariable String keyspace,
        @PathVariable String table
    ) {
        clusterTableCommander.tableDrop(clusterId, keyspace, table);

        return ApiResponse.ok();
    }

    //권한 추가해서 ADMIN 만 동작할 수 있도록 해야함.
    @DeleteMapping("/table/{table}/truncate")
    public ApiResponse<Void> tableTruncate(
        @PathVariable String clusterId,
        @PathVariable String keyspace,
        @PathVariable String table
    ) {
        clusterTableCommander.tableTruncate(clusterId, keyspace, table);

        return ApiResponse.ok();
    }

//    @PostMapping("/table/{table}/row/insert/query")
//    public ApiResponse<Map<String, Object>> tableInsertQuery(
//        @PathVariable String clusterId,
//        @PathVariable String keyspace,
//        @PathVariable String table,
//        @RequestBody Map<String, Object> row
//    ) {
//        Map<String, Object> map = new HashMap<>();
//        try (CqlSession session = tempClusterConnector.makeSession(clusterId)) {
//            Map<String, Term> newAssignments = new HashMap<>();
//            for (Map.Entry<String, Object> entry : row.entrySet()) {
//                newAssignments.put(entry.getKey(), QueryBuilder.raw(String.valueOf(entry.getValue())));
//            }
//
//            RegularInsert insert = QueryBuilder.insertInto(keyspace, table)
//                .values(newAssignments);
//
//
//            map.put("query", insert.build().getQuery());
//        }
//
//        return ApiResponse.ok(map);
//    }

}
