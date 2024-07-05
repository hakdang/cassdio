package kr.hakdang.cadio.web.route.cluster.table;

import com.datastax.oss.driver.api.core.CqlSession;
import kr.hakdang.cadio.core.domain.cluster.TempClusterConnector;
import kr.hakdang.cadio.core.domain.cluster.keyspace.table.ClusterTable;
import kr.hakdang.cadio.core.domain.cluster.keyspace.table.ClusterTableArgs.ClusterTableGetArgs;
import kr.hakdang.cadio.core.domain.cluster.keyspace.table.ClusterTableArgs.ClusterTableListArgs;
import kr.hakdang.cadio.core.domain.cluster.keyspace.table.ClusterTableGetCommander;
import kr.hakdang.cadio.core.domain.cluster.keyspace.table.ClusterTableGetResult;
import kr.hakdang.cadio.core.domain.cluster.keyspace.table.ClusterTableListCommander;
import kr.hakdang.cadio.core.domain.cluster.keyspace.table.ClusterTableListResult;
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
public class ClusterTableReader {

    private final TempClusterConnector tempClusterConnector;
    private final ClusterTableListCommander clusterTableListCommander;
    private final ClusterTableGetCommander clusterTableGetCommander;

    public ClusterTableReader(
        TempClusterConnector tempClusterConnector,
        ClusterTableListCommander clusterTableListCommander,
        ClusterTableGetCommander clusterTableGetCommander
    ) {
        this.tempClusterConnector = tempClusterConnector;
        this.clusterTableListCommander = clusterTableListCommander;
        this.clusterTableGetCommander = clusterTableGetCommander;
    }

    @Cacheable(value = CacheType.CacheTypeNames.TABLE_LIST, condition = "#cursorRequest.cursor == null")
    public ItemListWithCursorResponse<ClusterTable, String> listTables(String clusterId, String keyspace, CursorRequest cursorRequest) {
        try (CqlSession session = tempClusterConnector.makeSession(clusterId)) {
            ClusterTableListResult result = clusterTableListCommander.listTables(session, ClusterTableListArgs.builder()
                .keyspace(keyspace)
                .pageSize(cursorRequest.getSize())
                .nextPageState(cursorRequest.getCursor())
                .build());

            return ItemListWithCursorResponse.of(result.getTables(), CursorResponse.withNext(result.getNextPageState()));
        }
    }

    @Cacheable(value = CacheType.CacheTypeNames.TABLE)
    public ClusterTableGetResult getTable(String clusterId, String keyspace, String table, boolean withTableDescribe) {
        try (CqlSession session = tempClusterConnector.makeSession(clusterId)) {
            return clusterTableGetCommander.getTable(session, ClusterTableGetArgs.builder()
                .keyspace(keyspace)
                .table(table)
                .withTableDescribe(withTableDescribe)
                .build());
        }
    }

}
