package kr.hakdang.cadio.core.domain.cluster.keyspace.table.column;

import com.datastax.oss.driver.api.core.cql.Row;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Column
 *
 * @author seungh0
 * @since 2024-07-01
 */
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Column {

    private String tableName;
    private String name;
    private ColumnKind kind;
    private ColumnClusteringOrder clusteringOrder;
    private int position;
    private String dataType;

    @Builder
    private Column(String tableName, String name, ColumnKind kind, ColumnClusteringOrder clusteringOrder, int position, String dataType) {
        this.tableName = tableName;
        this.name = name;
        this.kind = kind;
        this.clusteringOrder = clusteringOrder;
        this.position = position;
        this.dataType = dataType;
    }

    public static Column from(Row row) {
        return Column.builder()
            .tableName(row.getString("table_name"))
            .name(row.getString("column_name"))
            .dataType(row.getString("type"))
            .kind(ColumnKind.findByCode(row.getString("kind")))
            .clusteringOrder(ColumnClusteringOrder.findByCode(row.getString("clustering_order")))
            .position(row.getInt("position"))
            .build();
    }

}
