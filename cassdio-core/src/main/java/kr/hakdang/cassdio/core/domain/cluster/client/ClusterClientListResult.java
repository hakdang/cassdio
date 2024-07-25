package kr.hakdang.cassdio.core.domain.cluster.client;

import com.datastax.oss.protocol.internal.util.Bytes;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * ClusterClientListResult
 *
 * @author seungh0
 * @since 2024-07-25
 */
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClusterClientListResult {

    private List<ClusterClient> clients;
    private String nextPageState;

    @Builder
    private ClusterClientListResult(List<ClusterClient> clients, ByteBuffer nextPageState) {
        this.clients = clients;
        this.nextPageState = nextPageState == null ? null : Bytes.toHexString(nextPageState);
    }

}
