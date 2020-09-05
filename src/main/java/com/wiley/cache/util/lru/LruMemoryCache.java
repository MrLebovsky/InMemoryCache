package com.wiley.cache.util.lru;

import com.wiley.cache.util.AbstractMemoryCache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A memory cache implementation which uses a LRU policy.
 */
public class LruMemoryCache extends AbstractMemoryCache {


    private static final int DEFAULT_CAPACITY = 10;

    private final Map<String, Object> map;

    public LruMemoryCache() {
        this(DEFAULT_CAPACITY);
    }

    public LruMemoryCache(int maxMemorySize) {
        super(maxMemorySize);
        this.map = new LruHashMap<>(DEFAULT_CAPACITY);
    }

    @Override
    public Object get(String key) {
        Objects.requireNonNull(key, "key == null");
        synchronized (this) {
            Object value = map.get(key);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    @Override
    public final Object put(String key, Object value) {
        Objects.requireNonNull(key, "key == null");
        Objects.requireNonNull(value, "value == null");
        Object previous;
        synchronized (this) {
            // предыдущее значение для ключа key, если он уже был в коллекции
            previous = map.put(key, value);
            memorySize += getValueSize(value);
            if (previous != null) {
                memorySize -= getValueSize(previous);
            }
            trimToSize(maxMemorySize);
        }
        return previous;
    }

    @Override
    public final Object remove(String key) {
        Objects.requireNonNull(key, "key == null");
        Object previous;
        synchronized (this) {
            previous = map.remove(key);
            if (previous != null) {
                memorySize -= getValueSize(previous);
            }
        }
        return previous;
    }

    /**
     * Returns a copy of the current contents of the cache.
     */
    public final LinkedHashMap<String, Object> snapshot() {
        return new LinkedHashMap<>(map);
    }

    /**
     * Remove the eldest entries.
     * <p>
     *
     * @param maxSize max size
     */
    @Override
    protected void trimToSize(int maxSize) {
        while (memorySize > maxSize && !map.isEmpty()) {
            if (memorySize < 0) {
                throw new IllegalStateException(
                        LruMemoryCache.class.getName() + ".getValueSize() is reporting inconsistent results"
                );
            }
            Map.Entry<String, Object> toRemove = map.entrySet().iterator().next();
            map.remove(toRemove.getKey());
            memorySize -= getValueSize(toRemove.getValue());
        }
    }
}
