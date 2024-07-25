package kr.hakdang.cassdio.core.domain.cluster.compaction;

import com.datastax.oss.driver.api.core.cql.Row;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

/**
 * CompactionHistory
 *
 * @author seungh0
 * @since 2024-07-25
 */
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompactionHistory {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private UUID id;
    private long bytesIn;
    private long bytesOut;
    private String columnFamilyName;
    private LocalDateTime compactedAt;
    private String keyspaceName;
    private Map<Integer, Long> rowMerged;

    @Builder
    private CompactionHistory(UUID id, long bytesIn, long bytesOut, String columnFamilyName, LocalDateTime compactedAt, String keyspaceName, Map<Integer, Long> rowMerged) {
        this.id = id;
        this.bytesIn = bytesIn;
        this.bytesOut = bytesOut;
        this.columnFamilyName = columnFamilyName;
        this.compactedAt = compactedAt;
        this.keyspaceName = keyspaceName;
        this.rowMerged = rowMerged;
    }

    public static CompactionHistory from(Row row) {
        return CompactionHistory.builder()
            .id(row.getUuid("id"))
            .bytesIn(row.getLong("bytes_in"))
            .bytesOut(row.getLong("bytes_out"))
            .compactedAt(LocalDateTime.ofInstant(row.get("compacted_at", Instant.class), TimeZone.getDefault().toZoneId()))
            .columnFamilyName(row.getString("columnfamily_name"))
            .keyspaceName(row.getString("keyspace_name"))
            .rowMerged(row.getMap("rows_merged", Integer.class, Long.class))
            .build();
    }

}
