package kr.hakdang.cassdio.core.domain.cluster.keyspace.udt;

import kr.hakdang.cassdio.IntegrationTest;
import kr.hakdang.cassdio.core.domain.cluster.CqlSessionFactory;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.udt.ClusterUDTTypeArgs.ClusterUDTTypeListArgs;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

/**
 * ClusterUDTTypeListCommanderTest
 *
 * @author seungh0
 * @since 2024-07-07
 */
class ClusterUDTTypeListCommanderTest extends IntegrationTest {

    @Autowired
    private ClusterUDTTypeListCommander clusterUDTTypeListCommander;
    @MockBean
    private CqlSessionFactory cqlSessionFactory;

    @Test
    void list_udt_types_in_keyspace() {
        // given
        given(cqlSessionFactory.get(anyString())).willReturn(makeSession());

        ClusterUDTTypeListArgs args = ClusterUDTTypeListArgs.builder()
            .keyspace(keyspaceName)
            .nextPageState(null)
            .build();

        // when
        ClusterUDTTypeListResult sut = clusterUDTTypeListCommander.listTypes(CLUSTER_ID, args);

        // then
        assertThat(sut.getTypes()).hasSize(1);
        assertThat(sut.getNextPageState()).isNull();
        assertThat(sut.getTypes().getFirst().getTypeName()).isEqualTo("test_type_1");
        assertThat(sut.getTypes().getFirst().getColumns()).hasSize(3);
        assertThat(sut.getTypes().getFirst().getColumns()).containsEntry("field_1", "text");
        assertThat(sut.getTypes().getFirst().getColumns()).containsEntry("field_2", "bigint");
        assertThat(sut.getTypes().getFirst().getColumns()).containsEntry("field_3", "time");
    }

    @Test
    void when_empty_type_exist_in_keyspace_return_empty_list() {
        // given
        given(cqlSessionFactory.get(anyString())).willReturn(makeSession());

        ClusterUDTTypeListArgs args = ClusterUDTTypeListArgs.builder()
            .keyspace("empty_keyspace")
            .nextPageState(null)
            .build();

        // when
        ClusterUDTTypeListResult sut = clusterUDTTypeListCommander.listTypes(CLUSTER_ID, args);

        // then
        assertThat(sut.getTypes()).isEmpty();
        assertThat(sut.getNextPageState()).isNull();
    }

}
