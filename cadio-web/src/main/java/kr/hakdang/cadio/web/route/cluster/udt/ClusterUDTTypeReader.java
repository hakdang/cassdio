package kr.hakdang.cadio.web.route.cluster.udt;

import com.datastax.oss.driver.api.core.CqlSession;
import kr.hakdang.cadio.core.domain.cluster.TempClusterConnector;
import kr.hakdang.cadio.core.domain.cluster.keyspace.udt.ClusterUDTType;
import kr.hakdang.cadio.core.domain.cluster.keyspace.udt.ClusterUDTTypeArgs.ClusterUDTTypeListArgs;
import kr.hakdang.cadio.core.domain.cluster.keyspace.udt.ClusterUDTTypeListCommander;
import kr.hakdang.cadio.core.domain.cluster.keyspace.udt.ClusterUDTTypeListResult;
import kr.hakdang.cadio.core.support.cache.CacheType;
import kr.hakdang.cadio.web.common.dto.request.CursorRequest;
import kr.hakdang.cadio.web.common.dto.response.CursorResponse;
import kr.hakdang.cadio.web.common.dto.response.ItemListWithCursorResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * ClusterTableReader
 *
 * @author seungh0
 * @since 2024-07-04
 */
@Service
public class ClusterUDTTypeReader {

    private final TempClusterConnector tempClusterConnector;
    private final ClusterUDTTypeListCommander clusterUDTTypeListCommander;

    public ClusterUDTTypeReader(
        TempClusterConnector tempClusterConnector,
        ClusterUDTTypeListCommander clusterUDTTypeListCommander
    ) {
        this.tempClusterConnector = tempClusterConnector;
        this.clusterUDTTypeListCommander = clusterUDTTypeListCommander;
    }

    @Cacheable(value = CacheType.CacheTypeNames.TYPE_LIST, condition = "#cursorRequest.cursor == null")
    public ItemListWithCursorResponse<ClusterUDTType, String> listTypes(String clusterId, String keyspace, CursorRequest cursorRequest) {
        try (CqlSession session = tempClusterConnector.makeSession(clusterId)) {
            ClusterUDTTypeListResult result = clusterUDTTypeListCommander.listTypes(session, ClusterUDTTypeListArgs.builder()
                .keyspace(keyspace)
                .pageSize(cursorRequest.getSize())
                .nextPageState(cursorRequest.getCursor())
                .build());

            return ItemListWithCursorResponse.of(result.getTypes(), CursorResponse.withNext(result.getNextPageState()));
        }
    }

}
