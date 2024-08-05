package kr.hakdang.cassdio.core.domain.cluster.node;

import com.datastax.oss.driver.api.core.metadata.NodeState;
import kr.hakdang.cassdio.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ClusterNodeListCommanderTest
 *
 * @author seungh0
 * @since 2024-07-03
 */
class ClusterNodeListCommanderTest extends IntegrationTest {

    @Autowired
    private ClusterNodeListCommander clusterNodeListCommander;

    @Test
    void list_nodes_in_cluster() {
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
