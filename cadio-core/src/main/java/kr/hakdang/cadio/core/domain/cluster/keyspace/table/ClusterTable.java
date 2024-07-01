package kr.hakdang.cadio.core.domain.cluster.keyspace.table;

import com.datastax.oss.driver.api.core.cql.Row;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * ClusterTable
 *
 * @author seungh0
 * @since 2024-07-01
 */
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClusterTable {

    private UUID id;
    private String tableName;
    private String comment;
    private Map<String, Object> options;

    @Builder
    public ClusterTable(UUID id, String tableName, String comment, Map<String, Object> options) {
        this.id = id;
        this.tableName = tableName;
        this.comment = comment;
        this.options = options;
    }

    public static ClusterTable from(Row row) {
        Map<String, Object> options = Arrays.stream(ClusterTableOptions.values())
            .collect(Collectors.toMap(option -> option.name().toLowerCase(Locale.ROOT), option -> option.extract(row)));

        return ClusterTable.builder()
            .id(row.getUuid("id"))
            .tableName(row.getString("table_name"))
            .comment(row.getString("comment"))
            .options(options)
            .build();
    }

}
