package kr.hakdang.cadio.core.support.cache;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;

/**
 * CacheType
 *
 * @author seungh0
 * @since 2024-07-04
 */
@Getter
public enum CacheType {

    // TODO: 만료시간은 5분으로 가져가돼, N분마다 api를 쏴서 클러스터에 대한 정보를 갱신하는 것도 좋을듯
    CLUSTER_LIST(CacheTypeNames.CLUSTER_LIST, Duration.ofMinutes(5)),
    CLUSTER(CacheTypeNames.CLUSTER, Duration.ofMinutes(5)),
    TABLE_LIST(CacheTypeNames.TABLE_LIST, Duration.ofMinutes(5)),
    TABLE(CacheTypeNames.TABLE, Duration.ofMinutes(5)),
    ;

    private final String key;
    private final Duration duration;

    CacheType(String key, Duration duration) {
        this.key = key;
        this.duration = duration;
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CacheTypeNames {

        public static final String CLUSTER_LIST = "clusterList";
        public static final String CLUSTER = "cluster";
        public static final String TABLE_LIST = "tableList";
        public static final String TABLE = "table";

    }

}
