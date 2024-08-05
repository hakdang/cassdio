package kr.hakdang.cassdio.core.domain.cluster;

import com.datastax.oss.driver.api.core.Version;
import kr.hakdang.cassdio.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ClusterVersionGetCommanderTest
 *
 * @author seungh0
 * @since 2024-07-26
 */
class ClusterVersionGetCommanderTest extends IntegrationTest {

    @Autowired
    private ClusterVersionGetCommander clusterVersionGetCommander;

    @Test
    void get_cassandra_version() {
        // when
        Version version = clusterVersionGetCommander.getCassandraVersion(CLUSTER_ID);

        // then
        assertThat(version).isNotNull();
        assertThat(version).isGreaterThanOrEqualTo(Version.parse("1.0.0"));
    }

}
