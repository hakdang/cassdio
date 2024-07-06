package kr.hakdang.cassdio.core.domain.cluster.keyspace.table;

import kr.hakdang.cassdio.IntegrationTest;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.ClusterTableArgs.ClusterTableGetArgs;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.column.ColumnClusteringOrder;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.column.ColumnKind;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ClusterTableGetCommanderTest
 *
 * @author seungh0
 * @since 2024-07-02
 */
@Slf4j
class ClusterTableGetCommanderTest extends IntegrationTest {

    @Autowired
    private ClusterTableGetCommander clusterTableGetCommander;

    @Test
    void getTable() {
        // given
        ClusterTableGetArgs args = ClusterTableGetArgs.builder()
            .keyspace(keyspaceName)
            .table("test_table_1")
            .build();

        // when
        ClusterTableGetResult sut = clusterTableGetCommander.getTable(makeSession(), args);

        // then
        assertThat(sut.getTable().getTableName()).isEqualTo("test_table_1");
        assertThat(sut.getTable().getComment()).isEqualTo("test_table_one");
        assertThat(sut.getTable().getOptions()).containsEntry("bloom_filter_fp_chance", 0.01);

        assertThat(sut.getColumns()).hasSize(5);
        assertThat(sut.getColumns().getFirst().getName()).isEqualTo("partition_key_1");
        assertThat(sut.getColumns().getFirst().getDataType()).isEqualTo("text");
        assertThat(sut.getColumns().getFirst().getClusteringOrder()).isEqualTo(ColumnClusteringOrder.NONE);
        assertThat(sut.getColumns().getFirst().getKind()).isEqualTo(ColumnKind.PARTITION_KEY);

        assertThat(sut.getColumns().get(1).getName()).isEqualTo("partition_key_2");
        assertThat(sut.getColumns().get(1).getDataType()).isEqualTo("bigint");
        assertThat(sut.getColumns().get(1).getClusteringOrder()).isEqualTo(ColumnClusteringOrder.NONE);
        assertThat(sut.getColumns().get(1).getKind()).isEqualTo(ColumnKind.PARTITION_KEY);

        assertThat(sut.getColumns().get(2).getName()).isEqualTo("clustering_key_1");
        assertThat(sut.getColumns().get(2).getDataType()).isEqualTo("bigint");
        assertThat(sut.getColumns().get(2).getClusteringOrder()).isEqualTo(ColumnClusteringOrder.DESC);
        assertThat(sut.getColumns().get(2).getKind()).isEqualTo(ColumnKind.CLUSTERING);

        assertThat(sut.getColumns().get(3).getName()).isEqualTo("clustering_key_2");
        assertThat(sut.getColumns().get(3).getDataType()).isEqualTo("text");
        assertThat(sut.getColumns().get(3).getClusteringOrder()).isEqualTo(ColumnClusteringOrder.ASC);
        assertThat(sut.getColumns().get(3).getKind()).isEqualTo(ColumnKind.CLUSTERING);

        assertThat(sut.getColumns().get(4).getName()).isEqualTo("column_1");
        assertThat(sut.getColumns().get(4).getDataType()).isEqualTo("text");
        assertThat(sut.getColumns().get(4).getClusteringOrder()).isEqualTo(ColumnClusteringOrder.NONE);
        assertThat(sut.getColumns().get(4).getKind()).isEqualTo(ColumnKind.REGULAR);
    }

}