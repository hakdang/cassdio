package kr.hakdang.cassdio.web.route.cluster.keyspace.table;

import com.datastax.oss.driver.api.core.CqlSession;
import kr.hakdang.cassdio.core.domain.cluster.TempClusterConnector;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.ClusterTable;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.ClusterTableArgs;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.ClusterTableArgs.ClusterTableListArgs;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.ClusterTableCommander;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.ClusterTableGetResult2;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.ClusterTableListCommander;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.ClusterTableListResult;
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
public class ClusterTableReader {

    private final TempClusterConnector tempClusterConnector;
    private final ClusterTableListCommander clusterTableListCommander;
    private final ClusterTableCommander clusterTableCommander;

    public ClusterTableReader(
        TempClusterConnector tempClusterConnector,
        ClusterTableListCommander clusterTableListCommander,
        ClusterTableCommander clusterTableCommander
    ) {
        this.tempClusterConnector = tempClusterConnector;
        this.clusterTableListCommander = clusterTableListCommander;
        this.clusterTableCommander = clusterTableCommander;
    }

    public ItemListWithCursorResponse<ClusterTable, String> listTables(String clusterId, String keyspace, CursorRequest cursorRequest) {
        try (CqlSession session = tempClusterConnector.makeSession(clusterId, keyspace)) {
            ClusterTableListResult result = clusterTableListCommander.listTables(session, ClusterTableListArgs.builder()
                .keyspace(keyspace)
                .pageSize(cursorRequest.getSize())
                .nextPageState(cursorRequest.getCursor())
                .build());

            return ItemListWithCursorResponse.of(result.getTables(), CursorResponse.withNext(result.getNextPageState()));
        }
    }

    public ClusterTableGetResult2 getTable(String clusterId, String keyspace, String table, boolean withTableDescribe) {
        try (CqlSession session = tempClusterConnector.makeSession(clusterId)) {

            return clusterTableCommander.tableDetail(session, ClusterTableArgs.ClusterTableGetArgs.builder()
                .keyspace(keyspace)
                .table(table)
                .withTableDescribe(withTableDescribe)
                .build());
        }
    }

}
