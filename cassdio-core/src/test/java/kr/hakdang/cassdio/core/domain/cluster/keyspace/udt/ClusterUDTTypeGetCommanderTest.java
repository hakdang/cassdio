package kr.hakdang.cassdio.core.domain.cluster.keyspace.udt;

import kr.hakdang.cassdio.IntegrationTest;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.udt.ClusterUDTTypeArgs.ClusterUDTTypeGetArgs;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.udt.ClusterUDTTypeException.ClusterUDTTypeNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * ClusterUDTTypeGetCommanderTest
 *
 * @author seungh0
 * @since 2024-07-07
 */
class ClusterUDTTypeGetCommanderTest extends IntegrationTest {

    @Autowired
    private ClusterUDTTypeGetCommander clusterUDTTypeGetCommander;

    @Test
    void get_udt_type_in_keyspace() {
        // given
        ClusterUDTTypeGetArgs args = ClusterUDTTypeGetArgs.builder()
            .keyspace(keyspaceName)
            .type("test_type_1")
            .build();

        // when
        ClusterUDTType sut = clusterUDTTypeGetCommander.getType(makeSession(), args);

        // then
        assertThat(sut.getTypeName()).isEqualTo("test_type_1");
        assertThat(sut.getColumns()).hasSize(3);
        assertThat(sut.getColumns()).containsEntry("field_1", "text");
        assertThat(sut.getColumns()).containsEntry("field_2", "bigint");
        assertThat(sut.getColumns()).containsEntry("field_3", "time");
    }

    @Test
    void when_get_not_exists_udt_type_in_keyspace_throw_not_exists_exception() {
        // given
        ClusterUDTTypeGetArgs args = ClusterUDTTypeGetArgs.builder()
            .keyspace(keyspaceName)
            .type("not_exist_type")
            .build();

        // when
        assertThatThrownBy(() -> clusterUDTTypeGetCommander.getType(makeSession(), args))
            .isInstanceOf(ClusterUDTTypeNotFoundException.class);
    }

    @Test
    void when_get_not_exists_udt_type_in_keyspace_throw_not_exists_exception_2() {
        // given
        ClusterUDTTypeGetArgs args = ClusterUDTTypeGetArgs.builder()
            .keyspace("another_keyspace")
            .type("test_type_1")
            .build();

        // when & then
        assertThatThrownBy(() -> clusterUDTTypeGetCommander.getType(makeSession(), args))
            .isInstanceOf(ClusterUDTTypeNotFoundException.class);
    }

}
