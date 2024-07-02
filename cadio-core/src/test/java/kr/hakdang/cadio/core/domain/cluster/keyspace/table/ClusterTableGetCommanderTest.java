package kr.hakdang.cadio.core.domain.cluster.keyspace.table;

import kr.hakdang.cadio.IntegrationTest;
import kr.hakdang.cadio.core.domain.cluster.keyspace.table.ClusterTableArgs.ClusterTableGetArgs;
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
    }

}
