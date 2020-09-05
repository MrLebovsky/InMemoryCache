package com.wiley.cache.util.lfu;

import com.wiley.cache.util.AbstractMemoryCache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A memory cache implementation which uses a LFU policy.
 */
public class LfuMemoryCache extends AbstractMemoryCache {

    private static final int DEFAULT_CAPACITY = 10;
    private final LinkedHashMap<String, LfuHashMapValue> linkedHashMap;

    public LfuMemoryCache(int maxMemorySize) {
        super(maxMemorySize);
        linkedHashMap = new LinkedHashMap<>(
                DEFAULT_CAPACITY,
                0.75f,
                true
        );
    }

    @Override
    public Object put(String key, Object value) {

        Objects.requireNonNull(key, "key == null");
        Objects.requireNonNull(value, "value == null");

        LfuHashMapValue lfuHashMapValue = new LfuHashMapValue();
        lfuHashMapValue.setValue(value);
        Object previous = linkedHashMap.put(key, lfuHashMapValue);

        memorySize += getValueSize(value);
        if (previous != null) {
            memorySize -= getValueSize(previous);
        }
        trimToSize(maxMemorySize);
        return previous;
    }

    @Override
    public Object remove(String key) {
        return null;
    }

    @Override
    public Object get(String key) {
        LfuHashMapValue lfuHashMapValue = linkedHashMap.get(key);
        lfuHashMapValue.incrementCountRequests();
        linkedHashMap.put(key, lfuHashMapValue);
        return lfuHashMapValue.getValue();
    }

    /**
     * Remove the entries with min requests count
     * <p>
     *
     * @param maxSize max size
     */
    @Override
    protected void trimToSize(int maxSize) {
        while (memorySize > maxSize && !linkedHashMap.isEmpty()) {
            if (memorySize < 0) {
                throw new IllegalStateException("memorySize less then zero");
            }
            Map.Entry<String, LfuHashMapValue> entryWithMinRequests = null;
            for (Map.Entry<String, LfuHashMapValue> entry : linkedHashMap.entrySet()) {
                if (entryWithMinRequests == null
                        || entryWithMinRequests.getValue().getRequestsCount()
                        > entry.getValue().getRequestsCount()) {
                    entryWithMinRequests = entry;
                }
            }
            if (entryWithMinRequests != null) {
                linkedHashMap.remove(entryWithMinRequests.getKey());
                memorySize -= getValueSize(entryWithMinRequests.getValue().getValue());
            }
        }
    }

    public int size() {
        return linkedHashMap.size();
    }

    public final LinkedHashMap<String, LfuHashMapValue> snapshot() {
        return linkedHashMap;
    }
}
