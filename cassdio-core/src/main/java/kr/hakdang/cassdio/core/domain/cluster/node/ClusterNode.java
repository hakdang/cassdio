package kr.hakdang.cassdio.core.domain.cluster.node;

import com.datastax.oss.driver.api.core.metadata.Node;
import com.datastax.oss.driver.api.core.metadata.NodeState;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

/**
 * ClusterNode
 *
 * @author seungh0
 * @since 2024-07-03
 */
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClusterNode {

    private UUID nodeId;
    private String hostIp;
    private String datacenter;
    private String rack;
    private String cassandraVersion;
    private NodeState nodeState;
    private long upSinceMillis;

    @Builder
    private ClusterNode(UUID nodeId, String hostIp, String datacenter, String rack, String cassandraVersion, NodeState nodeState, long upSinceMillis) {
        this.nodeId = nodeId;
        this.hostIp = hostIp;
        this.datacenter = datacenter;
        this.rack = rack;
        this.cassandraVersion = cassandraVersion;
        this.nodeState = nodeState;
        this.upSinceMillis = upSinceMillis;
    }

    public static ClusterNode from(Node node) {
        return ClusterNode.builder()
            .nodeId(node.getHostId())
            .hostIp(node.getEndPoint().toString())
            .datacenter(node.getDatacenter())
            .rack(node.getRack())
            .cassandraVersion(String.valueOf(node.getCassandraVersion()))
            .nodeState(node.getState())
            .upSinceMillis(node.getUpSinceMillis())
            .build();
    }

}
