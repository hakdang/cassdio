package kr.hakdang.cassdio.core.domain.cluster.keyspace.table;

import kr.hakdang.cassdio.IntegrationTest;
import kr.hakdang.cassdio.core.domain.cluster.CqlSessionFactory;
import kr.hakdang.cassdio.core.domain.cluster.CqlSessionSelectResult;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.ClusterTableException.ClusterTableNotFoundException;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.TableDTO.ClusterTableGetArgs;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

/**
 * ClusterTableGetCommanderTest
 *
 * @author seungh0
 * @since 2024-07-02
 */
@Slf4j
class ClusterTableGetCommanderTest extends IntegrationTest {

    private final ClusterTableCommander clusterTableCommander;

    @MockBean
    private CqlSessionFactory cqlSessionFactory;

    @Autowired
    private ClusterTableGetCommander clusterTableGetCommander;

    ClusterTableGetCommanderTest(ClusterTableCommander clusterTableCommander) {
        this.clusterTableCommander = clusterTableCommander;
    }

    @Test
    @Disabled
//TODO 변경필요
    void get_table_in_keyspace() {
        given(cqlSessionFactory.get(anyString())).willReturn(makeSession());

        ClusterTableGetArgs args = ClusterTableGetArgs.builder()
            .keyspace(keyspaceName)
            .table("test_table_1")
            .build();

        // when
        CqlSessionSelectResult sut = clusterTableGetCommander.tableDetail(CLUSTER_ID, args);

        // then
//        assertThat(sut.getTableDescribe()).isNotBlank();
//
//        assertThat(sut.getTable().getTableName()).isEqualTo("test_table_1");
//        assertThat(sut.getTable().getComment()).isEqualTo("test_table_one");
//        assertThat(sut.getTable().getOptions()).containsEntry("bloom_filter_fp_chance", 0.01);
//
//        assertThat(sut.getColumns()).hasSize(5);
//        assertThat(sut.getColumns().getFirst().getName()).isEqualTo("partition_key_1");
//        assertThat(sut.getColumns().getFirst().getDataType()).isEqualTo("text");
//        assertThat(sut.getColumns().getFirst().getClusteringOrder()).isEqualTo(ColumnClusteringOrder.NONE);
//        assertThat(sut.getColumns().getFirst().getKind()).isEqualTo(ColumnKind.PARTITION_KEY);
//
//        assertThat(sut.getColumns().get(1).getName()).isEqualTo("partition_key_2");
//        assertThat(sut.getColumns().get(1).getDataType()).isEqualTo("bigint");
//        assertThat(sut.getColumns().get(1).getClusteringOrder()).isEqualTo(ColumnClusteringOrder.NONE);
//        assertThat(sut.getColumns().get(1).getKind()).isEqualTo(ColumnKind.PARTITION_KEY);
//
//        assertThat(sut.getColumns().get(2).getName()).isEqualTo("clustering_key_1");
//        assertThat(sut.getColumns().get(2).getDataType()).isEqualTo("bigint");
//        assertThat(sut.getColumns().get(2).getClusteringOrder()).isEqualTo(ColumnClusteringOrder.DESC);
//        assertThat(sut.getColumns().get(2).getKind()).isEqualTo(ColumnKind.CLUSTERING);
//
//        assertThat(sut.getColumns().get(3).getName()).isEqualTo("clustering_key_2");
//        assertThat(sut.getColumns().get(3).getDataType()).isEqualTo("text");
//        assertThat(sut.getColumns().get(3).getClusteringOrder()).isEqualTo(ColumnClusteringOrder.ASC);
//        assertThat(sut.getColumns().get(3).getKind()).isEqualTo(ColumnKind.CLUSTERING);
//
//        assertThat(sut.getColumns().get(4).getName()).isEqualTo("column_1");
//        assertThat(sut.getColumns().get(4).getDataType()).isEqualTo("text");
//        assertThat(sut.getColumns().get(4).getClusteringOrder()).isEqualTo(ColumnClusteringOrder.NONE);
//        assertThat(sut.getColumns().get(4).getKind()).isEqualTo(ColumnKind.REGULAR);
    }

    @Test
    void when_get_not_exists_table_throw_not_exists_exception() {
        // given
        given(cqlSessionFactory.get(anyString())).willReturn(makeSession());
        TableDTO.ClusterTableGetArgs args = TableDTO.ClusterTableGetArgs.builder()
            .keyspace(keyspaceName)
            .table("not_exists_table")
            .build();

        // when & then
        assertThatThrownBy(() -> clusterTableGetCommander.tableDetail(CLUSTER_ID, args)).isInstanceOf(ClusterTableNotFoundException.class);

    }

    @Test
    void get_system_table_describe() {
        // given
        given(cqlSessionFactory.get(anyString())).willReturn(makeSession());

        // when
        String sut = clusterTableCommander.tableDescribe(CLUSTER_ID, "system_schema", "tables");

        // then
        assertThat(sut).isBlank();
        //assertThat(sut.getTable().getTableName()).isEqualTo("tables");
    }

}
