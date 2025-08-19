package kopo.poly.config;

import kopo.poly.dto.WeatherDTO;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.Caching;
import javax.cache.CacheManager;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public org.springframework.cache.CacheManager cacheManager() {
        // Ehcache JSR-107 Provider 사용
        CachingProvider provider =
                Caching.getCachingProvider("org.ehcache.jsr107.EhcacheCachingProvider");
        CacheManager jcacheManager = provider.getCacheManager();

        // JCache 설정: 키/값 타입, TTL(5분), by-reference(성능 위해)
        MutableConfiguration<String, WeatherDTO> cfg = new MutableConfiguration<String, WeatherDTO>()
                .setTypes(String.class, WeatherDTO.class)
                .setStoreByValue(false) // by reference
                .setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(
                        new Duration(TimeUnit.MINUTES, 5)));

        // 캐시가 없으면 생성
        if (jcacheManager.getCache("weather", String.class, WeatherDTO.class) == null) {
            jcacheManager.createCache("weather", cfg);
        }

        // Spring Cache가 사용할 CacheManager 래퍼 반환
        return new JCacheCacheManager(jcacheManager);
    }
}
