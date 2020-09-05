package com.wiley.cache.util;

public abstract class AbstractMemoryCache {

    /**
     *The flag represents remove all entries in the cache.
     */
    private static final int REMOVE_ALL = -1;
    protected final int maxMemorySize;
    protected int memorySize;

    protected AbstractMemoryCache(int maxMemorySize) {
        this.maxMemorySize = maxMemorySize;
    }

    /**
     * Puts an value in the cache for the specified {@code key}.
     *
     * @param key   key
     * @param value image
     * @return the previous value.
     */
    public abstract Object put(String key, Object value);

    /**
     * Gets an value for the specified {@code key} or return {@code null}.
     *
     * @param key key
     * @return the value or {@code null}.
     */
    public abstract Object get(String key);

    /**
     * Removes the entry for {@code key} if it exists or return {@code null}.
     *
     * @return the previous value or @{code null}.
     */
    public abstract Object remove(String key);

    /**
     * Remove the entries with special algorithm
     * @param maxSize threshold elements
     */
    protected abstract void trimToSize(int maxSize);

    /**
     * Returns the max memory size of the cache.
     *
     * @return max memory size.
     */
    public final int getMaxMemorySize() {
        return maxMemorySize;
    }

    /**
     * Returns the current memory size of the cache.
     *
     * @return current memory size.
     */
    public final int getMemorySize() {
        return memorySize;
    }

    /**
     * Clears all the entries in the cache.
     */
    public void clear() {
        trimToSize(REMOVE_ALL);
    }

    /**
     * Returns the size of the entry.
     * <p>
     * The default implementation returns 1 so that max size is the maximum number of entries.
     * <p>
     * <em>Note:</em> This method should be overridden if you control memory size correctly.
     *
     * @param value value
     * @return the size of the entry.
     */
    protected int getValueSize(Object value) {
        return 1;
    }
}
