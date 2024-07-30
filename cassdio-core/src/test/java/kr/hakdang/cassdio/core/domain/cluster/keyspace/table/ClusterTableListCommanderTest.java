package kr.hakdang.cassdio.core.domain.cluster.keyspace.table;

import kr.hakdang.cassdio.IntegrationTest;
import kr.hakdang.cassdio.core.domain.cluster.CqlSessionFactory;
import kr.hakdang.cassdio.core.domain.cluster.CqlSessionSelectResults;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestConstructor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

/**
 * ClusterTableListCommanderTest
 *
 * @author seungh0
 * @since 2024-07-01
 */
@Slf4j
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class ClusterTableListCommanderTest extends IntegrationTest {

    @MockBean
    private CqlSessionFactory cqlSessionFactory;

    private final ClusterTableListCommander clusterTableListCommander;

    public ClusterTableListCommanderTest(ClusterTableListCommander clusterTableListCommander) {
        this.clusterTableListCommander = clusterTableListCommander;
    }

    @Test
    void list_tables() {
        // given
        given(cqlSessionFactory.get(anyString())).willReturn(makeSession());

        TableDTO.ClusterTableListArgs args = TableDTO.ClusterTableListArgs.builder()
            .keyspace(keyspaceName)
            .pageSize(50)
            .build();

        // when
        CqlSessionSelectResults sut = clusterTableListCommander.tableList(CLUSTER_ID, args);

        // then
        assertThat(sut).isNotNull();
        assertThat(sut.getRows()).hasSizeGreaterThanOrEqualTo(1);
        assertThat(sut.getRowHeader()).hasSizeGreaterThanOrEqualTo(1);
//        assertThat(sut.getTables().getFirst().getTableName()).isEqualTo("test_table_1");
//        assertThat(sut.getTables().getFirst().getComment()).isEqualTo("test_table_one");
//        assertThat(sut.getTables().getFirst().getOptions()).containsEntry("bloom_filter_fp_chance", 0.01);
//
//        assertThat(sut.getTables().get(1).getTableName()).isEqualTo("test_table_2");
//        assertThat(sut.getTables().get(1).getComment()).isEqualTo("test_table_two");
//        assertThat(sut.getTables().get(1).getOptions()).containsEntry("bloom_filter_fp_chance", 0.001);
    }

    @Test
    void listTables_with_limit() {
        // given
        given(cqlSessionFactory.get(anyString())).willReturn(makeSession());

        TableDTO.ClusterTableListArgs args = TableDTO.ClusterTableListArgs.builder()
            .keyspace(keyspaceName)
            .pageSize(1)
            .build();

        // when
        CqlSessionSelectResults sut = clusterTableListCommander.tableList(CLUSTER_ID, args);

        // then
        assertThat(sut).isNotNull();
        assertThat(sut.getRows()).hasSize(1);
//        assertThat(sut.getTables()).hasSize(1);
//        assertThat(sut.getTables().getFirst().getTableName()).isEqualTo("test_table_1");
//        assertThat(sut.getTables().getFirst().getComment()).isEqualTo("test_table_one");
//        assertThat(sut.getTables().getFirst().getOptions()).containsEntry("bloom_filter_fp_chance", 0.01);
//        assertThat(sut.getNextPageState()).isNotBlank();
    }

    @Test
    void when_empty_table_in_keyspace_result_empty() {
        // given
        given(cqlSessionFactory.get(anyString())).willReturn(makeSession());

        TableDTO.ClusterTableListArgs args = TableDTO.ClusterTableListArgs.builder()
            .keyspace("empty_table_keyspace")
            .build();

        // when
        CqlSessionSelectResults sut = clusterTableListCommander.tableList(CLUSTER_ID, args);

        // then
        assertThat(sut).isNotNull();
        assertThat(sut.getRows()).hasSize(0);
//        assertThat(sut.getNextPageState()).isNull();
    }

}
