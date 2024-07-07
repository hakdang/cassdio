package kr.hakdang.cassdio.core.domain.cluster.keyspace;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CassandraSystemKeyspaceTest
 *
 * @author seungh0
 * @since 2024-07-05
 */
class CassandraSystemKeyspaceTest {

    @Test
    void check_is_system_keyspace() {
        for (CassandraSystemKeyspace keyspace : CassandraSystemKeyspace.values()) {
            assertThat(CassandraSystemKeyspace.isSystemKeyspace(keyspace.getKeyspaceName())).isTrue();
        }

        assertThat(CassandraSystemKeyspace.isSystemKeyspace("demo")).isFalse();
    }

    @Test
    void check_is_virtual_system_keyspace() {
        assertThat(CassandraSystemKeyspace.isVirtualSystemKeyspace("system_virtual_schema")).isTrue();
        assertThat(CassandraSystemKeyspace.isVirtualSystemKeyspace("system_views")).isTrue();
        assertThat(CassandraSystemKeyspace.isVirtualSystemKeyspace("system_traces")).isFalse();
        assertThat(CassandraSystemKeyspace.isVirtualSystemKeyspace("demo")).isFalse();
    }

}
