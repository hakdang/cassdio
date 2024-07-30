package kr.hakdang.cassdio.core.domain.cluster.client;

import com.datastax.oss.driver.api.core.Version;
import kr.hakdang.cassdio.BaseTest;
import kr.hakdang.cassdio.common.error.NotSupportedCassandraVersionException;
import kr.hakdang.cassdio.core.domain.cluster.ClusterVersionCommander;
import kr.hakdang.cassdio.core.domain.cluster.CqlSessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * ClusterClientListCommanderTest
 *
 * @author seungh0
 * @since 2024-07-26
 */
class ClusterClientListCommanderTest extends BaseTest {

    @Mock
    private ClusterVersionCommander clusterVersionCommander;

    @MockBean
    private CqlSessionFactory cqlSessionFactory;

    private ClusterClientListCommander clusterClientListCommander;

    @BeforeEach
    void setUp() {
        clusterClientListCommander = new ClusterClientListCommander(clusterVersionCommander, cqlSessionFactory);
    }

    @Test
    void not_supported_under_v4_0_0() {
        // given
        when(clusterVersionCommander.getCassandraVersion(anyString())).thenReturn(Version.V3_0_0);

        assertThatThrownBy(() -> clusterClientListCommander.getClients(CLUSTER_ID))
            .isInstanceOf(NotSupportedCassandraVersionException.class);
    }

}
