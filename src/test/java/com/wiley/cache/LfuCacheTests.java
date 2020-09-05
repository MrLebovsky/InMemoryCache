package com.wiley.cache;

import com.wiley.cache.util.lfu.LfuMemoryCache;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class LfuCacheTests {

    String GOOGLE = "GOOGLE";
    String IBM = "IBM";
    String INTEL = "INTEL";
    String MICROSOFT = "MICROSOFT";
    String TESLA = "TESLA";

    LfuMemoryCache testCacheFactory() {
        // Initialize cash object with maxMemorySize = 4 objects
        LfuMemoryCache lfuCache = new LfuMemoryCache( 4);

        // Fill the data
        lfuCache.put(GOOGLE, GOOGLE);
        lfuCache.put(IBM, IBM);
        lfuCache.put(INTEL, INTEL);
        lfuCache.put(MICROSOFT, MICROSOFT);

        return lfuCache;
    }

    @Test
    void mainLogic() {

        LfuMemoryCache lfuCache = testCacheFactory();

        assertThat(lfuCache.size()).isEqualTo(4);

        // Request for some cash objects except 'GOOGLE' object
        String microsoftVar = (String) lfuCache.get(MICROSOFT);
        lfuCache.get(IBM);
        lfuCache.get(INTEL);

        // Check the get method returned correct value
        assertThat(microsoftVar).isEqualTo(MICROSOFT);

        // Add the new one object and cache memory should become oversize
        lfuCache.put(TESLA, TESLA);
        assertThat(lfuCache.size()).isEqualTo(4);

        // Check the object with minimal request count was deleted
        assertThat(lfuCache.snapshot().containsKey(GOOGLE)).isEqualTo(false);
    }

    @Test
    void clearElements() {
        LfuMemoryCache lfuCache = testCacheFactory();
        lfuCache.clear();
        assertThat(lfuCache.size()).isEqualTo(0);
    }
}
