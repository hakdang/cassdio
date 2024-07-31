package kr.hakdang.cassdio.core.domain.cluster.client;

import com.datastax.oss.driver.api.core.Version;
import kr.hakdang.cassdio.BaseTest;
import kr.hakdang.cassdio.common.error.NotSupportedCassandraVersionException;
import kr.hakdang.cassdio.core.domain.cluster.ClusterVersionEvaluator;
import kr.hakdang.cassdio.core.domain.cluster.CqlSessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * ClusterClientListCommanderTest
 *
 * @author seungh0
 * @since 2024-07-26
 */
class ClusterClientListCommanderTest extends BaseTest {

    @Mock
    private ClusterVersionEvaluator clusterVersionEvaluator;

    @Mock
    private CqlSessionFactory cqlSessionFactory;

    private ClusterClientListCommander clusterClientListCommander;

    @BeforeEach
    void setUp() {
        clusterClientListCommander = new ClusterClientListCommander(clusterVersionEvaluator, cqlSessionFactory);
    }

    @Test
    void not_supported_under_v4_0_0() {
        // given
        when(clusterVersionEvaluator.isLessThan(CLUSTER_ID, Version.V4_0_0)).thenReturn(true);

        assertThatThrownBy(() -> clusterClientListCommander.getClients(CLUSTER_ID))
            .isInstanceOf(NotSupportedCassandraVersionException.class);
    }

}
