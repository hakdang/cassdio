package kr.hakdang.cassdio.core.support.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * LocalCacheConfig
 *
 * @author seungh0
 * @since 2024-07-04
 */
@EnableCaching
@Configuration
public class LocalCacheConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        List<CaffeineCache> caches = Arrays.stream(CacheType.values())
            .map(cache -> new CaffeineCache(cache.getKey(), Caffeine.newBuilder()
                .expireAfterWrite(cache.getDuration())
                .build()
            ))
            .toList();
        cacheManager.setCaches(caches);
        return cacheManager;
    }

}
