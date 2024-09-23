package kr.hakdang.cassdio.web.route.cluster.keyspace.table;

import jakarta.servlet.http.HttpServletResponse;
import kr.hakdang.cassdio.core.domain.cluster.CqlSessionSelectResults;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.CassdioColumnDefinition;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.ClusterCsvProvider;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.ClusterTableRowCommander;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.TableDTO;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.column.ClusterTableColumnCommander;
import kr.hakdang.cassdio.web.common.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVPrinter;
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
@Slf4j
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

    @PostMapping("/table/{table}/row/import/sample")
    public void importerSampleDownload(
        HttpServletResponse response,
        @PathVariable String clusterId,
        @PathVariable String keyspace,
        @PathVariable String table
    ) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("text/csv; charset=UTF-8");
        String exportFileName = "sample-" + LocalDateTime.now() + ".csv"; //TODO : Formatting

        response.setHeader("Content-disposition", "attachment;filename=" + exportFileName);

        List<String> columnList = clusterTableColumnCommander.columnSortedList(clusterId, keyspace, table);

        clusterCsvProvider.importerCsvSampleDownload(response.getWriter(), columnList);
    }

    @PostMapping("/table/{table}/row/import")
    public ApiResponse<Map<String, Object>> importUpload(
        @PathVariable String clusterId,
        @PathVariable String keyspace,
        @PathVariable String table,
        @RequestParam("file") MultipartFile file,
        ClusterTableRequest.TableRowImportRequest request
    ) throws IOException {

        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            List<String> columnList = clusterTableColumnCommander.columnSortedList(clusterId, keyspace, table);

            List<Map<String, Object>> values = clusterCsvProvider.importCsvReader(
                reader, columnList
            );

            clusterTableRowCommander.rowInserts(
                TableDTO.ClusterTableRowImportArgs.builder()
                    .clusterId(clusterId)
                    .keyspace(keyspace)
                    .table(table)
                    .batchTypeCode(request.getBatchTypeCode())
                    .perCommitSize(request.getPerCommitSize())
                    .build()
                , values);
        }


        return ApiResponse.ok(emptyMap());
    }

    @PostMapping("/table/{table}/row/export")
    public void rowExporterDownload(
        HttpServletResponse response,
        @PathVariable String clusterId,
        @PathVariable String keyspace,
        @PathVariable String table
    ) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("text/csv; charset=UTF-8");

        String exportFileName = keyspace + "-" + table + "-" + LocalDateTime.now() + ".csv";

        response.setHeader("Content-disposition", "attachment;filename=" + exportFileName);


        TableDTO.ClusterTableRowArgs args = TableDTO.ClusterTableRowArgs.builder()
            .keyspace(keyspace)
            .table(table)
            .build();

        CqlSessionSelectResults results = clusterTableRowCommander.rowSelect(clusterId, args);

        List<String> headerList = results.getRowHeader()
            .stream()
            .map(CassdioColumnDefinition::getColumnName)
            .toList();

        clusterCsvProvider.exporterCsvDownload(response.getWriter(), headerList, csvPrinter -> {
            try {
                setData(csvPrinter, headerList, results.getRows());

                if (results.hasNext()) {
                    boolean hasNext;
                    String cursor = results.getNextCursor();
                    do {

                        TableDTO.ClusterTableRowArgs subArgs = TableDTO.ClusterTableRowArgs.builder()
                            .keyspace(keyspace)
                            .table(table)
                            .cursor(cursor)
                            .build();

                        CqlSessionSelectResults subResult = clusterTableRowCommander.rowSelect(clusterId, subArgs);
                        hasNext = subResult.hasNext();
                        if (hasNext) {
                            cursor = subResult.getNextCursor();
                        }

                        setData(csvPrinter, headerList, subResult.getRows());

                    } while (hasNext);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void setData(CSVPrinter csvPrinter, List<String> headerList, List<Map<String, Object>> rows) throws IOException {
        for (Map<String, Object> row : rows) {
            List<String> data = headerList.stream()
                .map(header -> row.getOrDefault(header, "").toString())
                .toList();

            csvPrinter.printRecord(data);
        }
    }
}
