package kr.hakdang.cassdio.web.route.cluster.keyspace.table;

import com.datastax.oss.driver.api.core.CqlSession;
import kr.hakdang.cassdio.core.domain.cluster.ClusterConnector;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.ClusterTable;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.ClusterTableArgs.ClusterTableListArgs;
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

    private final ClusterConnector clusterConnector;
    private final ClusterTableListCommander clusterTableListCommander;

    public ClusterTableReader(
        ClusterConnector clusterConnector,
        ClusterTableListCommander clusterTableListCommander
    ) {
        this.clusterConnector = clusterConnector;
        this.clusterTableListCommander = clusterTableListCommander;
    }

    public ItemListWithCursorResponse<ClusterTable, String> listTables(String clusterId, String keyspace, CursorRequest cursorRequest) {
        try (CqlSession session = clusterConnector.makeSession(clusterId, keyspace)) {
            ClusterTableListResult result = clusterTableListCommander.listTables(session, ClusterTableListArgs.builder()
                .keyspace(keyspace)
                .pageSize(cursorRequest.getSize())
                .nextPageState(cursorRequest.getCursor())
                .build());

            return ItemListWithCursorResponse.of(result.getTables(), CursorResponse.withNext(result.getNextPageState()));
        }
    }

}
