package kr.hakdang.cadio.core.domain.cluster.keyspace.table;

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
public class ClusterTableListResult {

    private List<ClusterTable> tables;
    private String nextPageState;

    public ClusterTableListResult(List<ClusterTable> tables, String nextPageState) {
        this.tables = tables;
        this.nextPageState = nextPageState;
    }

    public static ClusterTableListResult of(List<ClusterTable> tables, ByteBuffer pagingState) {
        return new ClusterTableListResult(tables, pagingState == null ? null : Bytes.toHexString(pagingState));
    }

}
