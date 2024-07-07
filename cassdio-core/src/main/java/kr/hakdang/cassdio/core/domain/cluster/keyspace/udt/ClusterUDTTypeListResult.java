package kr.hakdang.cassdio.core.domain.cluster.keyspace.udt;

import com.datastax.oss.protocol.internal.util.Bytes;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * ClusterTableListResult
 *
 * @author seungh0
 * @since 2024-07-01
 */
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClusterUDTTypeListResult {

    private List<ClusterUDTType> types;
    private String nextPageState;

    public ClusterUDTTypeListResult(List<ClusterUDTType> types, String nextPageState) {
        this.types = types;
        this.nextPageState = nextPageState;
    }

    public static ClusterUDTTypeListResult of(List<ClusterUDTType> tables, ByteBuffer pagingState) {
        return new ClusterUDTTypeListResult(tables, pagingState == null ? null : Bytes.toHexString(pagingState));
    }

}
