package com.lyferise.cube.concurrency;

import java.util.concurrent.atomic.AtomicLongArray;

public class CacheLineValue {
    private static final int VALUES_PER_CACHE_LINE = 8;
    private final AtomicLongArray buffer = new AtomicLongArray(2 * VALUES_PER_CACHE_LINE);

    public long get() {
        return buffer.get(VALUES_PER_CACHE_LINE);
    }

    public void set(final long value) {
        buffer.set(VALUES_PER_CACHE_LINE, value);
    }

    public boolean compareAndSet(final long expectedValue, final long newValue) {
        return buffer.compareAndSet(VALUES_PER_CACHE_LINE, expectedValue, newValue);
    }

    @Override
    public String toString() {
        return Long.toString(get());
    }
}