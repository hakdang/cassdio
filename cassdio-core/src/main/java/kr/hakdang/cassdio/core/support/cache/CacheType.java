package kr.hakdang.cassdio.core.support.cache;

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

    CLUSTER_DETAIL(CacheTypeNames.CLUSTER_DETAIL, Duration.ofMinutes(1)),
    ;

    private final String key;
    private final Duration duration;

    CacheType(String key, Duration duration) {
        this.key = key;
        this.duration = duration;
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CacheTypeNames {

        public static final String CLUSTER_DETAIL = "clusterDetail";

    }

}
