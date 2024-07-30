package kr.hakdang.cassdio.core.domain.cluster.node;

import com.datastax.oss.driver.api.core.metadata.NodeState;
import kr.hakdang.cassdio.IntegrationTest;
import kr.hakdang.cassdio.core.domain.cluster.CqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

/**
 * ClusterNodeListCommanderTest
 *
 * @author seungh0
 * @since 2024-07-03
 */
class ClusterNodeListCommanderTest extends IntegrationTest {

    @Autowired
    private ClusterNodeListCommander clusterNodeListCommander;

    @MockBean
    private CqlSessionFactory cqlSessionFactory;

    @Test
    void list_nodes_in_cluster() {
        given(cqlSessionFactory.get(anyString())).willReturn(makeSession());

        // given
        List<ClusterNode> nodes = clusterNodeListCommander.listNodes(CLUSTER_ID);

        assertThat(nodes).hasSize(1);
        assertThat(nodes.getFirst().getRack()).isEqualTo("rack1");
        assertThat(nodes.getFirst().getNodeState()).isEqualTo(NodeState.UP);
        assertThat(nodes.getFirst().getDatacenter()).isEqualTo("dc1");
        assertThat(nodes.getFirst().getUpSinceMillis()).isGreaterThan(0);
        assertThat(nodes.getFirst().getCassandraVersion()).isNotNull();
    }

}
