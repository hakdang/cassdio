package kr.hakdang.cassdio.core.domain.cluster;

import com.datastax.oss.driver.api.core.Version;
import kr.hakdang.cassdio.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

/**
 * ClusterVersionCommanderTest
 *
 * @author seungh0
 * @since 2024-07-26
 */
class ClusterVersionCommanderTest extends IntegrationTest {

    @Autowired
    private ClusterVersionCommander clusterVersionCommander;

    @MockBean
    private CqlSessionFactory cqlSessionFactory;

    @Test
    void get_cassandra_version() {
        given(cqlSessionFactory.get(anyString())).willReturn(makeSession());

        // when
        Version version = clusterVersionCommander.getCassandraVersion(CLUSTER_ID);

        // then
        assertThat(version).isNotNull();
        assertThat(version).isGreaterThanOrEqualTo(Version.parse("1.0.0"));
    }

}
