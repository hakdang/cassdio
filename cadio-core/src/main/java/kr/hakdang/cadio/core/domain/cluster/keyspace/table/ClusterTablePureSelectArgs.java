package kr.hakdang.cadio.core.domain.cluster.keyspace.table;

import kr.hakdang.cadio.core.domain.cluster.ClusterCommandArgs;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ClusterTablePureSelectArgs
 *
 * @author akageun
 * @since 2024-06-30
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClusterTablePureSelectArgs
    implements ClusterCommandArgs {

    private String keyspace;
    private String table;

    private long limit; //TODO : max check
}
