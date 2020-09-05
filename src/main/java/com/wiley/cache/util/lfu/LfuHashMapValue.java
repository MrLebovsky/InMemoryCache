package com.wiley.cache.util.lfu;

public final class LfuHashMapValue {

    Integer requestsCount = 0;
    Object value;

    public Integer getRequestsCount() {
        return requestsCount;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void incrementCountRequests() {
        requestsCount++;
    }
}

