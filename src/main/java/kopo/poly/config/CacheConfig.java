package kopo.poly.config;

import kopo.poly.dto.WeatherDTO;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;     // JCache(javax.cache) → Spring Cache 어댑터
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.Caching;                                  // CachingProvider 팩토리
import javax.cache.CacheManager;                             // JCache 표준 CacheManager
import javax.cache.configuration.MutableConfiguration;       // 캐시 설정(키/값 타입, 저장 방식 등)
import javax.cache.expiry.CreatedExpiryPolicy;               // 생성 시점을 기준으로 만료되는 정책
import javax.cache.expiry.Duration;                          // 만료 기간(시간 단위)
import javax.cache.spi.CachingProvider;                      // 구현체(Ehcache JSR-107 Provider 등)

import java.util.concurrent.TimeUnit;

/**
 * Spring Boot 3 환경에서 JCache(JSR-107) + Ehcache 3 를
 * 스프링 캐시 추상화에 연결하는 설정 클래스.
 * <p>
 * - @EnableCaching : @Cacheable/@CacheEvict 등 애노테이션 활성화
 * - Ehcache JSR-107 Provider를 명시적으로 선택하여 JCache 표준 API로 캐시를 사용
 * - "weather" 캐시를 생성하고 TTL을 10분으로 지정
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Spring의 CacheManager 빈을 등록합니다.
     * <p>
     * 반환 타입을 org.springframework.cache.CacheManager 로 둔 이유:
     * - 스프링 캐시 추상화(@Cacheable 등)가 이 타입을 사용함
     * - 내부적으로는 JCache(javax.cache) CacheManager를 감싸는 어댑터(JCacheCacheManager)를 리턴
     */
    @Bean
    public org.springframework.cache.CacheManager cacheManager() {

        // 1) JCache CachingProvider 획득
        //    - FQCN을 지정해 Ehcache의 JSR-107 Provider를 직접 선택
        //    - 클래스명: org.ehcache.jsr107.EhcacheCachingProvider
        //    - 만약 기본 Provider를 쓰고 싶다면 인자 없는 Caching.getCachingProvider() 호출도 가능
        CachingProvider provider =
                Caching.getCachingProvider("org.ehcache.jsr107.EhcacheCachingProvider");

        // 2) 표준 JCache CacheManager 얻기
        //    - classpath 내 ehcache.xml 등 외부 설정이 있으면 Provider가 읽어들일 수 있음
        CacheManager jcacheManager = provider.getCacheManager();

        // 3) "weather" 캐시에 적용할 설정 구성
        //    - 키/값 타입을 명시(setTypes)하면 캐시에 잘못된 타입이 들어가는 것을 방지(런타임 타입 세이프)
        //    - setStoreByValue(false) : "by reference" 저장
        //        * 장점: 직렬화/복사 비용이 없어 빠름
        //        * 주의: 캐시에서 꺼낸 객체를 외부에서 변경하면 캐시 안의 값도 같이 변경됨(공유 참조)
        //          → DTO는 불변(immutable) 또는 읽기 전용으로 사용하는 것을 권장
        //    - ExpiryPolicy: CreatedExpiryPolicy → "생성(또는 put) 시점 기준" TTL 부여
        //      Duration(분 단위) : 요구사항에 맞춰 10분으로 설정
        MutableConfiguration<String, WeatherDTO> cfg =
                new MutableConfiguration<String, WeatherDTO>()
                        .setTypes(String.class, WeatherDTO.class)
                        .setStoreByValue(false) // by reference (성능 위주, 가변 객체 주의)
                        .setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(
                                new Duration(TimeUnit.MINUTES, 10)  // ★ TTL = 10분 (5분 사용 시 5로 변경)
                        ));

        // 4) 캐시 존재 여부 확인 후 없으면 생성
        //    - 동일 이름/타입의 캐시가 이미 있으면 재생성하지 않음(예외 방지)
        if (jcacheManager.getCache("weather", String.class, WeatherDTO.class) == null) {
            jcacheManager.createCache("weather", cfg);
        }

        // 5) 스프링 캐시 추상화 어댑터로 감싸서 반환
        //    - 이후 서비스 메서드에서 @Cacheable(cacheNames = "weather") 등으로 사용 가능
        //    - JCacheCacheManager는 내부적으로 위 jcacheManager 를 위임 사용
        return new JCacheCacheManager(jcacheManager);
    }
}
