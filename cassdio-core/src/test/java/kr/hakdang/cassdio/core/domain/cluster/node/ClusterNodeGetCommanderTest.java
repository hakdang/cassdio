package kr.hakdang.cassdio.core.domain.cluster.node;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.metadata.Node;
import kr.hakdang.cassdio.IntegrationTest;
import kr.hakdang.cassdio.core.domain.cluster.ClusterException.ClusterNodeNotFoundException;
import kr.hakdang.cassdio.core.domain.cluster.CqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

/**
 * ClusterNodeGetCommanderTest
 *
 * @author seungh0
 * @since 2024-07-03
 */
class ClusterNodeGetCommanderTest extends IntegrationTest {

    @Autowired
    private ClusterNodeGetCommander clusterNodeGetCommander;

    @MockBean
    private CqlSessionFactory cqlSessionFactory;

    @Test
    void not_exists_node_in_cluster() {
        given(cqlSessionFactory.get(anyString())).willReturn(makeSession());

        // when & then
        assertThatThrownBy(() -> clusterNodeGetCommander.getNode(CLUSTER_ID, UUID.randomUUID()))
            .isInstanceOf(ClusterNodeNotFoundException.class);
    }

    @Test
    void get_node_in_cluster() {
        // given
        CqlSession session = makeSession();
        given(cqlSessionFactory.get(anyString())).willReturn(session);

        Map<UUID, Node> nodes = session.getMetadata().getNodes();

        for (Map.Entry<UUID, Node> node : nodes.entrySet()) {
            ClusterNode sut = clusterNodeGetCommander.getNode(CLUSTER_ID, node.getKey());

            assertThat(sut.getNodeId()).isEqualTo(node.getValue().getHostId());
            assertThat(sut.getDatacenter()).isEqualTo("dc1");
            assertThat(sut.getRack()).isEqualTo("rack1");
            assertThat(sut.getUpSinceMillis()).isGreaterThan(0);
            assertThat(sut.getCassandraVersion()).isNotNull();
            assertThat(sut.getCassandraVersion()).isEqualTo(String.valueOf(node.getValue().getCassandraVersion()));
        }
    }

}
