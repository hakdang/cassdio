package kr.hakdang.cassdio.core.domain.cluster.keyspace.table;

import kr.hakdang.cassdio.IntegrationTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ClusterTableListCommanderTest
 *
 * @author seungh0
 * @since 2024-07-01
 */
@Slf4j
public class ClusterTableListCommanderTest extends IntegrationTest {

    @Autowired
    private ClusterTableListCommander clusterTableListCommander;

    @Test
    void list_tables() {
        // given
        ClusterTableArgs.ClusterTableListArgs args = ClusterTableArgs.ClusterTableListArgs.builder()
            .keyspace(keyspaceName)
            .pageSize(50)
            .build();

        // when
        ClusterTableListResult sut = clusterTableListCommander.listTables(makeSession(), args);

        // then
        assertThat(sut.getTables()).hasSize(2);
        assertThat(sut.getTables().getFirst().getTableName()).isEqualTo("test_table_1");
        assertThat(sut.getTables().getFirst().getComment()).isEqualTo("test_table_one");
        assertThat(sut.getTables().getFirst().getOptions()).containsEntry("bloom_filter_fp_chance", 0.01);

        assertThat(sut.getTables().get(1).getTableName()).isEqualTo("test_table_2");
        assertThat(sut.getTables().get(1).getComment()).isEqualTo("test_table_two");
        assertThat(sut.getTables().get(1).getOptions()).containsEntry("bloom_filter_fp_chance", 0.001);
    }

    @Test
    void listTables_with_limit() {
        // given
        ClusterTableArgs.ClusterTableListArgs args = ClusterTableArgs.ClusterTableListArgs.builder()
            .keyspace(keyspaceName)
            .pageSize(1)
            .build();

        // when
        ClusterTableListResult sut = clusterTableListCommander.listTables(makeSession(), args);

        // then
        assertThat(sut.getTables()).hasSize(1);
        assertThat(sut.getTables().getFirst().getTableName()).isEqualTo("test_table_1");
        assertThat(sut.getTables().getFirst().getComment()).isEqualTo("test_table_one");
        assertThat(sut.getTables().getFirst().getOptions()).containsEntry("bloom_filter_fp_chance", 0.01);
        assertThat(sut.getNextPageState()).isNotBlank();
    }

    @Test
    void when_empty_table_in_keyspace_result_empty() {
        // given
        ClusterTableArgs.ClusterTableListArgs args = ClusterTableArgs.ClusterTableListArgs.builder()
            .keyspace("empty_table_keyspace")
            .build();

        // when
        ClusterTableListResult sut = clusterTableListCommander.listTables(makeSession(), args);

        // then
        assertThat(sut.getTables()).isEmpty();
        assertThat(sut.getNextPageState()).isNull();
    }

}
