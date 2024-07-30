package kr.hakdang.cassdio.web.route.cluster.keyspace.udt;

import kr.hakdang.cassdio.core.domain.cluster.keyspace.udt.ClusterUDTType;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.udt.ClusterUDTTypeArgs.ClusterUDTTypeGetArgs;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.udt.ClusterUDTTypeArgs.ClusterUDTTypeListArgs;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.udt.ClusterUDTTypeGetCommander;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.udt.ClusterUDTTypeListCommander;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.udt.ClusterUDTTypeListResult;
import kr.hakdang.cassdio.web.common.dto.request.CursorRequest;
import kr.hakdang.cassdio.web.common.dto.response.CursorResponse;
import kr.hakdang.cassdio.web.common.dto.response.ItemListWithCursorResponse;
import org.springframework.stereotype.Service;

/**
 * ClusterTableReader
 *
 * @author seungh0
 * @since 2024-07-04
 */
@Service
public class ClusterUDTTypeReader {
    private final ClusterUDTTypeListCommander clusterUDTTypeListCommander;
    private final ClusterUDTTypeGetCommander clusterUDTTypeGetCommander;

    public ClusterUDTTypeReader(
        ClusterUDTTypeListCommander clusterUDTTypeListCommander,
        ClusterUDTTypeGetCommander clusterUDTTypeGetCommander
    ) {
        this.clusterUDTTypeListCommander = clusterUDTTypeListCommander;
        this.clusterUDTTypeGetCommander = clusterUDTTypeGetCommander;
    }

    public ItemListWithCursorResponse<ClusterUDTType, String> listTypes(String clusterId, String keyspace, CursorRequest cursorRequest) {
        ClusterUDTTypeListResult result = clusterUDTTypeListCommander.listTypes(clusterId, ClusterUDTTypeListArgs.builder()
            .keyspace(keyspace)
            .pageSize(cursorRequest.getSize())
            .nextPageState(cursorRequest.getCursor())
            .build());

        return ItemListWithCursorResponse.of(result.getTypes(), CursorResponse.withNext(result.getNextPageState()));
    }

    public ClusterUDTType getType(String clusterId, String keyspace, String type) {
        return clusterUDTTypeGetCommander.getType(clusterId, ClusterUDTTypeGetArgs.builder()
            .keyspace(keyspace)
            .type(type)
            .build());
    }

}
