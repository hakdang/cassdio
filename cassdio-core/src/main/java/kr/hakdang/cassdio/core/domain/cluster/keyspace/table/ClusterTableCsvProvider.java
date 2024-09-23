package kr.hakdang.cassdio.core.domain.cluster.keyspace.table;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * ClusterTableCsvProvider
 *
 * @author akageun
 * @since 2024-08-08
 */
@Slf4j
@Service
public class ClusterTableCsvProvider {

    public void importerCsvSampleDownload(Writer writer, List<String> sortedColumnList) {
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
            .setHeader(sortedColumnList.toArray(String[]::new))
            .build();

        try (final CSVPrinter printer = new CSVPrinter(writer, csvFormat)) {
            //printer.printRecord(author, title);
            log.info("create complete importer csv sample");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
