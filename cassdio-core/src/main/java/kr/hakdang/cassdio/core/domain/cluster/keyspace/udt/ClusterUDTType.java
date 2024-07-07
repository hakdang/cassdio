package kr.hakdang.cassdio.core.domain.cluster.keyspace.udt;

import com.datastax.oss.driver.api.core.cql.Row;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static kr.hakdang.cassdio.core.domain.cluster.keyspace.table.column.CassandraSystemTablesColumn.TYPES_FIELD_NAMES;
import static kr.hakdang.cassdio.core.domain.cluster.keyspace.table.column.CassandraSystemTablesColumn.TYPES_FIELD_TYPES;
import static kr.hakdang.cassdio.core.domain.cluster.keyspace.table.column.CassandraSystemTablesColumn.TYPES_TYPE_NAME;

/**
 * ClusterUDTType
 *
 * @author Seungho Kang (will.seungho@webtoonscorp.com)
 * @version 1.0.0
 * @since 2024. 07. 06.
 */
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClusterUDTType {

    private String typeName;
    private Map<String, String> columns;

    private ClusterUDTType(String typeName, Map<String, String> columns) {
        this.typeName = typeName;
        this.columns = columns;
    }

    public static ClusterUDTType from(Row row) {
        List<String> fieldNames = row.getList(TYPES_FIELD_NAMES.getColumnName(), String.class);
        List<String> fieldDataTypes = row.getList(TYPES_FIELD_TYPES.getColumnName(), String.class);

        if (fieldNames == null || fieldDataTypes == null) {
            throw new RuntimeException("Unexpected Exception. fieldNames or fieldDataTypes is null");
        }

        if (fieldNames.size() != fieldDataTypes.size()) {
            throw new RuntimeException("Unexpected Exception. fieldNames and fieldDataTypes is not matched");
        }

        Map<String, String> columns = IntStream.range(0, fieldNames.size())
            .mapToObj(i -> Pair.of(fieldNames.get(i), fieldDataTypes.get(i)))
            .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));

        return new ClusterUDTType(row.getString(TYPES_TYPE_NAME.getColumnName()), columns);
    }

}
