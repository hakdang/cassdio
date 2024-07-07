package kr.hakdang.cassdio.core.domain.cluster.keyspace;

import kr.hakdang.cassdio.IntegrationTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ClusterKeyspaceCommanderTest
 *
 * @author seungh0
 * @since 2024-07-07
 */
@Slf4j
class ClusterKeyspaceCommanderTest extends IntegrationTest {

    @Autowired
    private ClusterKeyspaceCommander clusterKeyspaceCommander;

    @Test
    void list_all_keyspace_names_include_system_keyspace() {
        List<String> sut = clusterKeyspaceCommander.allKeyspaceNames(makeSession(), true);

        // then
        assertThat(sut).contains("testdb");
        assertThat(sut).contains(CassandraSystemKeyspace.SYSTEM.getKeyspaceName(), CassandraSystemKeyspace.SYSTEM_SCHEMA.getKeyspaceName());
    }

    @Test
    void list_all_keyspace_names_exclude_system_keyspace() {
        List<String> sut = clusterKeyspaceCommander.allKeyspaceNames(makeSession(), false);

        // then
        assertThat(sut).contains("testdb");
        assertThat(sut).doesNotContain(CassandraSystemKeyspace.SYSTEM.getKeyspaceName(), CassandraSystemKeyspace.SYSTEM_SCHEMA.getKeyspaceName());
    }

}
