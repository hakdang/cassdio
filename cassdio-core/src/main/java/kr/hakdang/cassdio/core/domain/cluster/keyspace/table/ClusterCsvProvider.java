package kr.hakdang.cassdio.core.domain.cluster.keyspace.table;

import com.datastax.oss.driver.api.core.cql.BatchStatement;
import com.datastax.oss.driver.api.core.cql.BatchType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
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

}
