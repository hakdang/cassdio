package kr.hakdang.cassdio.web.route.cluster.keyspace.udt;

import jakarta.validation.Valid;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.udt.ClusterUDTType;
import kr.hakdang.cassdio.web.common.dto.request.CursorRequest;
import kr.hakdang.cassdio.web.common.dto.response.ApiResponse;
import kr.hakdang.cassdio.web.common.dto.response.ItemListWithCursorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClusterUDTTypeApi
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

    @GetMapping("/udt-type/{type}")
    public ApiResponse<ClusterUDTType> listTables(
        @PathVariable String clusterId,
        @PathVariable String keyspace,
        @PathVariable String type
    ) {
        ClusterUDTType result = clusterUDTTypeReader.getType(clusterId, keyspace, type);
        return ApiResponse.ok(result);
    }

}
