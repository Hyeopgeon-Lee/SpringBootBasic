package kopo.poly.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CacheConfigTests {

    @Test
    void initializesWeatherAndNoticeCachesWithEhcache() {
        CacheConfig config = new CacheConfig();
        var manager = config.cacheManager();

        assertNotNull(manager.getCache("weather"));
        assertNotNull(manager.getCache("notice:list"));
    }
}
