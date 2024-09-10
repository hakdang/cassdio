package kr.hakdang.cassdio.core.domain.cluster.keyspace.table;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * ClusterTableCsvProvider
 *
 * @author akageun
 * @since 2024-08-08
 */
@Slf4j
@Service
public class ClusterCsvProvider {

    public void importerCsvSampleDownload(Writer writer, List<String> headerList) {
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
            .setHeader(headerList.toArray(String[]::new))
            .build();

        try (final CSVPrinter printer = new CSVPrinter(writer, csvFormat)) {
            log.info("create complete importer csv sample");

            printer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public List<Map<String, Object>> importCsvReader(Reader reader, List<String> columnList) throws IOException {
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
            .setHeader(columnList.toArray(String[]::new))
            .setSkipHeaderRecord(true)
            .setTrim(true)
            .build();

        Iterable<CSVRecord> records = csvFormat.parse(reader);
        //Validation 방식에 대해 고민 필요

        List<Map<String, Object>> values = new ArrayList<>();

        for (CSVRecord record : records) {

            Map<String, Object> map = new HashMap<>();

            for (String column : columnList) {
                map.put(column, StringUtils.defaultIfBlank(record.get(column), ""));
            }

            values.add(map);
        }

        return values;
    }

    public void exporterCsvDownload(Writer writer, List<String> headerList, Consumer<CSVPrinter> csvPrinterConsumer) {
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
            .setHeader(headerList.toArray(String[]::new))
            .build();

        try (final CSVPrinter printer = new CSVPrinter(writer, csvFormat)) {
            csvPrinterConsumer.accept(printer);
            printer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
