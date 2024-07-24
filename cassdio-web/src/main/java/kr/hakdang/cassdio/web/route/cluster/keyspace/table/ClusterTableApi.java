package kr.hakdang.cassdio.web.route.cluster.keyspace.table;

import com.datastax.oss.driver.api.core.CqlSession;
import jakarta.validation.Valid;
import kr.hakdang.cassdio.core.domain.cluster.CqlSessionSelectResults;
import kr.hakdang.cassdio.core.domain.cluster.ClusterConnector;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.ClusterTableListArgs;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.ClusterTableArgs;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.ClusterTableCommander;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.column.ClusterTableColumnCommander;
import kr.hakdang.cassdio.web.common.dto.request.CursorRequest;
import kr.hakdang.cassdio.web.common.dto.response.ApiResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.bindMarker;

/**
 * ClusterTableApi
 *
 * @author seungh0
 * @since 2024-07-01
 */
@RestController
@RequestMapping("/api/cassandra/cluster/{clusterId}/keyspace/{keyspace}")
public class ClusterTableApi {

    private final ClusterTableProvider clusterTableProvider;
    private final ClusterConnector clusterConnector;
    private final ClusterTableCommander clusterTableCommander;
    private final ClusterTableColumnCommander clusterTableColumnCommander;

    public ClusterTableApi(
        ClusterTableProvider clusterTableProvider,
        ClusterConnector clusterConnector,
        ClusterTableCommander clusterTableCommander,
        ClusterTableColumnCommander clusterTableColumnCommander
    ) {
        this.clusterTableProvider = clusterTableProvider;
        this.clusterConnector = clusterConnector;
        this.clusterTableCommander = clusterTableCommander;
        this.clusterTableColumnCommander = clusterTableColumnCommander;
    }

    @GetMapping("/table")
    public ApiResponse<Map<String, Object>> listTables(
        @PathVariable String clusterId,
        @PathVariable String keyspace,
        @Valid CursorRequest cursorRequest
    ) {
        Map<String, Object> result = new HashMap<>();
        try (CqlSession session = clusterConnector.makeSession(clusterId)) {
            ClusterTableListArgs args = ClusterTableListArgs.builder()
                .keyspace(keyspace)
                .pageSize(cursorRequest.getSize())
                .cursor(cursorRequest.getCursor())
                .build();

            CqlSessionSelectResults tableListResult = clusterTableCommander.allTables(session, args);

            result.put("tableList", tableListResult);
        }

        return ApiResponse.ok(result);
    }

    @GetMapping("/table/{table}")
    public ApiResponse<Map<String, Object>> getTable(
        @PathVariable String clusterId,
        @PathVariable String keyspace,
        @PathVariable String table
    ) {
        Map<String, Object> result = new HashMap<>();

        try (CqlSession session = clusterConnector.makeSession(clusterId, keyspace)) {

            ClusterTableArgs.ClusterTableGetArgs tableGetArgs = ClusterTableArgs.ClusterTableGetArgs.builder()
                .keyspace(keyspace)
                .table(table)
                .build();

            result.put("detail", clusterTableCommander.tableDetail(session, tableGetArgs));

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

        try (CqlSession session = clusterConnector.makeSession(clusterId, keyspace)) {
            result.put("columnList", clusterTableColumnCommander.columnList(session, keyspace, table));
        }

        return ApiResponse.ok(result);
    }

    @GetMapping("/table/{table}/row")
    public ApiResponse<Map<String, Object>> tablePureTableSelect(
        @PathVariable String clusterId,
        @PathVariable String keyspace,
        @PathVariable String table,
        @ModelAttribute ClusterTablePureSelectRequest request
    ) {
        Map<String, Object> map = new HashMap<>();
        try (CqlSession session = clusterConnector.makeSession(clusterId)) {
            CqlSessionSelectResults result1 = clusterTableCommander.pureSelect(session, request.makeArgs(keyspace, table));

            map.put("nextCursor", result1.getNextCursor());
            map.put("rows", result1.getRows());
            map.put("rowHeader", result1.getRowHeader());

            map.put("columnList", clusterTableColumnCommander.columnList(session, keyspace, table));
        }

        return ApiResponse.ok(map);
    }

    //권한 추가해서 ADMIN 만 동작할 수 있도록 해야함.
    @DeleteMapping("/table/{table}")
    public ApiResponse<Void> tableDrop(
        @PathVariable String clusterId,
        @PathVariable String keyspace,
        @PathVariable String table
    ) {
        try (CqlSession session = clusterConnector.makeSession(clusterId)) { //TODO : interface 작업할 때 facade layer 로 변경 예정

            clusterTableCommander.tableDrop(session, keyspace, table);

            return ApiResponse.ok();
        }
    }

    //권한 추가해서 ADMIN 만 동작할 수 있도록 해야함.
    @DeleteMapping("/table/{table}/truncate")
    public ApiResponse<Void> tableTruncate(
        @PathVariable String clusterId,
        @PathVariable String keyspace,
        @PathVariable String table
    ) {
        try (CqlSession session = clusterConnector.makeSession(clusterId)) { //TODO : interface 작업할 때 facade layer 로 변경 예정

            clusterTableCommander.tableTruncate(session, keyspace, table);

            return ApiResponse.ok();
        }
    }

//    @PostMapping("/table/{table}/row/insert/query")
//    public ApiResponse<Map<String, Object>> tableInsertQuery(
//        @PathVariable String clusterId,
//        @PathVariable String keyspace,
//        @PathVariable String table,
//        @RequestBody Map<String, Object> row
//    ) {
//        Map<String, Object> map = new HashMap<>();
//        try (CqlSession session = tempClusterConnector.makeSession(clusterId)) { //TODO : interface 작업할 때 facade layer 로 변경 예정
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
