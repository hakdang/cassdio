package kr.hakdang.cassdio.web.route.cluster.keyspace.table;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.insert.InsertInto;
import com.datastax.oss.driver.api.querybuilder.insert.RegularInsert;
import com.datastax.oss.driver.api.querybuilder.term.Term;
import jakarta.validation.Valid;
import kr.hakdang.cassdio.core.domain.cluster.CqlSessionSelectResults;
import kr.hakdang.cassdio.core.domain.cluster.TempClusterConnector;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.ClusterTable;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.ClusterTableArgs;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.ClusterTableCommander;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.column.ClusterTableColumnCommander;
import kr.hakdang.cassdio.web.common.dto.request.CursorRequest;
import kr.hakdang.cassdio.web.common.dto.response.ApiResponse;
import kr.hakdang.cassdio.web.common.dto.response.ItemListWithCursorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/table/{table}/row")
    public ApiResponse<Map<String, Object>> tablePureTableSelect(
        @PathVariable String clusterId,
        @PathVariable String keyspace,
        @PathVariable String table,
        @ModelAttribute ClusterTablePureSelectRequest request
    ) {
        Map<String, Object> map = new HashMap<>();
        try (CqlSession session = tempClusterConnector.makeSession(clusterId)) { //TODO : interface 작업할 때 facade layer 로 변경 예정
            CqlSessionSelectResults result1 = clusterTableCommander.pureSelect(session, request.makeArgs(keyspace, table));

            map.put("nextCursor", result1.getNextCursor());
            map.put("rows", result1.getRows());
            map.put("columnHeader", result1.getColumnHeader());
        }

        return ApiResponse.ok(map);
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
