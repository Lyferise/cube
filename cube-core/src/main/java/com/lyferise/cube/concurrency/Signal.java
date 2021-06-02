package com.lyferise.cube.concurrency;

import lombok.SneakyThrows;

public class Signal {
    private final Object syncRoot = new Object();

    public void set() {
        synchronized (syncRoot) {
            syncRoot.notify();
        }
    }

    @SneakyThrows
    public void await(final long timeoutMillis) {
        synchronized (syncRoot) {
            syncRoot.wait(timeoutMillis);
        }
    }
}