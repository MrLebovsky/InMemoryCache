package com.wiley.cache;

import com.wiley.cache.util.lru.LruMemoryCache;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
public class LruCacheTests {

    private static final String A = "A";

    private static final String B = "B";

    private static final String C = "C";

    private static final String D = "D";

    private static final String E = "E";

    private LruMemoryCache cache;

    @Before
    public void setUp() {
        cache = new LruMemoryCache(3);
    }

    @After
    public void tearDown() {
        cache.clear();
        cache = null;
    }
    private static void assertMiss(LruMemoryCache cache, String key) {
        assertNull(cache.get(key));
    }

    private static void assertHit(LruMemoryCache cache, String key, String value) {
        assertThat(cache.get(key)).isEqualTo(value);
    }

    private static void assertSnapshot(LruMemoryCache cache, String... keysAndValues) {
        List<String> actualKeysAndValues = new ArrayList<>();
        for (Map.Entry<String, Object> entry : cache.snapshot().entrySet()) {
            actualKeysAndValues.add(entry.getKey());
            actualKeysAndValues.add(entry.getValue().toString());
        }
        assertEquals(Arrays.asList(keysAndValues), actualKeysAndValues);
    }

    @Test
    public void mainLogic() {
        cache.put("a", A);
        assertHit(cache, "a", A);
        cache.put("b", B);
        assertHit(cache, "a", A);
        assertHit(cache, "b", B);
        assertSnapshot(cache, "a", A, "b", B);

        cache.put("c", C);
        assertHit(cache, "a", A);
        assertHit(cache, "b", B);
        assertHit(cache, "c", C);
        assertSnapshot(cache, "a", A, "b", B, "c", C);

        cache.put("d", D);
        assertMiss(cache, "a");
        assertHit(cache, "b", B);
        assertHit(cache, "c", C);
        assertHit(cache, "d", D);
        assertHit(cache, "b", B);
        assertHit(cache, "c", C);
        assertSnapshot(cache, "d", D, "b", B, "c", C);

        cache.put("e", E);
        assertMiss(cache, "d");
        assertMiss(cache, "a");
        assertHit(cache, "e", E);
        assertHit(cache, "b", B);
        assertHit(cache, "c", C);
        assertSnapshot(cache, "e", E, "b", B, "c", C);
    }

}
