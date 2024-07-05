package kr.hakdang.cadio.web.route.cluster.udt;

import jakarta.validation.Valid;
import kr.hakdang.cadio.core.domain.cluster.keyspace.udt.ClusterUDTType;
import kr.hakdang.cadio.web.common.dto.request.CursorRequest;
import kr.hakdang.cadio.web.common.dto.response.ApiResponse;
import kr.hakdang.cadio.web.common.dto.response.ItemListWithCursorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClusterTableApi
 *
 * @author seungh0
 * @since 2024-07-01
 */
@RestController
@RequestMapping("/api/cassandra/cluster/{clusterId}/keyspace/{keyspace}")
public class ClusterUDTTypeApi {

    private final ClusterUDTTypeReader clusterUDTTypeReader;

    public ClusterUDTTypeApi(ClusterUDTTypeReader clusterUDTTypeReader) {
        this.clusterUDTTypeReader = clusterUDTTypeReader;
    }

    @GetMapping("/udt-type")
    public ApiResponse<ItemListWithCursorResponse<ClusterUDTType, String>> listTables(
        @PathVariable String clusterId,
        @PathVariable String keyspace,
        @Valid CursorRequest cursorRequest
    ) {
        ItemListWithCursorResponse<ClusterUDTType, String> tables = clusterUDTTypeReader.listTypes(clusterId, keyspace, cursorRequest);
        return ApiResponse.ok(tables);
    }

}
