package kr.hakdang.cassdio.core.domain.cluster;

import com.datastax.oss.driver.api.core.Version;
import kr.hakdang.cassdio.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ClusterVersionCommanderTest
 *
 * @author seungh0
 * @since 2024-07-26
 */
class ClusterVersionCommanderTest extends IntegrationTest {

    @Autowired
    private ClusterVersionCommander clusterVersionCommander;

    @Test
    void get_cassandra_version() {
        // when
        Version version = clusterVersionCommander.getCassandraVersion(makeSession());

        // then
        assertThat(version).isNotNull();
        assertThat(version).isGreaterThanOrEqualTo(Version.parse("1.0.0"));
    }

}
