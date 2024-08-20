package kr.hakdang.cassdio.web.route.cluster.keyspace.table;

import jakarta.servlet.http.HttpServletResponse;
import kr.hakdang.cassdio.core.domain.cluster.CqlSessionSelectResults;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.ClusterCsvProvider;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.ClusterTableRowCommander;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.column.ClusterTableColumnCommander;
import kr.hakdang.cassdio.web.common.dto.response.ApiResponse;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyMap;

/**
 * ClusterTableRowApi
 *
 * @author akageun
 * @since 2024-08-19
 */
@RestController
@RequestMapping("/api/cassandra/cluster/{clusterId}/keyspace/{keyspace}")
public class ClusterTableRowApi {

    private final ClusterTableRowCommander clusterTableRowCommander;
    private final ClusterCsvProvider clusterCsvProvider;
    private final ClusterTableColumnCommander clusterTableColumnCommander;

    public ClusterTableRowApi(
        ClusterTableRowCommander clusterTableRowCommander,
        ClusterCsvProvider clusterCsvProvider,
        ClusterTableColumnCommander clusterTableColumnCommander
    ) {
        this.clusterTableRowCommander = clusterTableRowCommander;
        this.clusterCsvProvider = clusterCsvProvider;
        this.clusterTableColumnCommander = clusterTableColumnCommander;
    }

    @GetMapping("/table/{table}/row")
    public ApiResponse<Map<String, Object>> tableRow(
        @PathVariable String clusterId,
        @PathVariable String keyspace,
        @PathVariable String table,
        @ModelAttribute ClusterTableRowRequest request
    ) {
        Map<String, Object> responseMap = new HashMap<>();
        CqlSessionSelectResults result1 = clusterTableRowCommander.rowSelect(clusterId, request.makeArgs(keyspace, table));

        responseMap.put("nextCursor", result1.getNextCursor());
        responseMap.put("rows", result1.getRows());
        responseMap.put("rowHeader", result1.getRowHeader());

        responseMap.put("columnList", clusterTableColumnCommander.columnList(clusterId, keyspace, table));

        return ApiResponse.ok(responseMap);
    }

    @PostMapping("/table/{table}/row/export")
    public ApiResponse<Map<String, Object>> tableRowExport(
        @PathVariable String clusterId,
        @PathVariable String keyspace,
        @PathVariable String table,
        @ModelAttribute ClusterTableRowRequest request
    ) {
        Map<String, Object> responseMap = new HashMap<>();

        return ApiResponse.ok(responseMap);
    }


    @PostMapping("/table/{table}/row/import/sample")
    public ApiResponse<Map<String, Object>> importerSampleDownload(
        HttpServletResponse response,
        @PathVariable String clusterId,
        @PathVariable String keyspace,
        @PathVariable String table
    ) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("text/csv; charset=UTF-8");
        String exportFileName = "sample-" + LocalDateTime.now() + ".csv";

        response.setHeader("Content-disposition", "attachment;filename=" + exportFileName);

        Map<String, Object> responseMap = new HashMap<>();

        List<String> columnList = clusterTableColumnCommander.columnSortedList(clusterId, keyspace, table);

        clusterCsvProvider.importerCsvSampleDownload(response.getWriter(), columnList);

        return ApiResponse.ok(responseMap);
    }

    @PostMapping("/table/{table}/row/import")
    public ApiResponse<Map<String, Object>> importDownload(
        HttpServletResponse response,
        @PathVariable String clusterId,
        @PathVariable String keyspace,
        @PathVariable String table,
        @RequestParam("file") MultipartFile file,
        ClusterTableRequest.TableRowImportRequest request
    ) throws IOException {
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            List<String> columnList = clusterTableColumnCommander.columnSortedList(clusterId, keyspace, table);

            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(columnList.toArray(String[]::new))
                .setSkipHeaderRecord(true)
                .build();

            Iterable<CSVRecord> records = csvFormat.parse(reader);
            for (CSVRecord record : records) {
                for (String column : columnList) {

                }
            }
        }


        List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
        List<String> headerList = new ArrayList<String>();


//        try {
//
//        } catch (IOException e) {
//            throw new RuntimeException(e); //TODO : 상세화
//        }

        return ApiResponse.ok(emptyMap());
    }
}
