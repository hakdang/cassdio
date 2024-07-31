package kr.hakdang.cassdio.core.domain.cluster;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * ClusterConnectionTest
 *
 * @author seungh0
 * @since 2024-07-07
 */
class ClusterConnectionTest {

    @Test
    void anonymous_cluster_connection() {
        // when
        ClusterConnection connection = ClusterConnection.builder()
            .contactPoints("127.0.0.1")
            .port(29042)
            .localDatacenter("dc1")
            .build();

        // then
        assertThat(connection.isAuthCredentials()).isFalse();
        assertThat(connection.getContactPoints()).isEqualTo("127.0.0.1");
        assertThat(connection.getPort()).isEqualTo(29042);
        assertThat(connection.getUsername()).isNull();
        assertThat(connection.getPassword()).isNull();
        assertThat(connection.getLocalDatacenter()).isEqualTo("dc1");
    }

    @Test
    void authenticated_cluster_connection() {
        // when
        ClusterConnection connection = ClusterConnection.builder()
            .contactPoints("127.0.0.1")
            .port(29042)
            .localDatacenter("dc1")
            .username("will")
            .password("will-password")
            .build();

        // then
        assertThat(connection.isAuthCredentials()).isTrue();
        assertThat(connection.getContactPoints()).isEqualTo("127.0.0.1");
        assertThat(connection.getPort()).isEqualTo(29042);
        assertThat(connection.getUsername()).isEqualTo("will");
        assertThat(connection.getPassword()).isEqualTo("will-password");
        assertThat(connection.getLocalDatacenter()).isEqualTo("dc1");
    }


    @EmptySource
    @ParameterizedTest
    void contactPoints_is_required(String contactPoints) {
        // when & then
        assertThatThrownBy(() ->
            ClusterConnection.builder()
                .contactPoints(contactPoints)
                .port(29042)
                .localDatacenter("dc1")
                .build()
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @ValueSource(ints = {
        0,
        -1
    })
    @ParameterizedTest
    void port_should_be_positive(int port) {
        // when & then
        assertThatThrownBy(() ->
            ClusterConnection.builder()
                .contactPoints("127.0.0.1")
                .port(port)
                .localDatacenter("dc1")
                .build()
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @EmptySource
    @ParameterizedTest
    void local_datacenter_is_required(String localDatacenter) {
        // when & then
        assertThatThrownBy(() ->
            ClusterConnection.builder()
                .contactPoints("127.0.0.1")
                .port(9042)
                .localDatacenter(localDatacenter)
                .build()
        ).isInstanceOf(IllegalArgumentException.class);
    }

}
