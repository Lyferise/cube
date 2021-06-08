package com.lyferise.cube.concurrency;

import lombok.SneakyThrows;

import java.util.concurrent.CountDownLatch;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class Signal {
    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    public void set() {
        countDownLatch.countDown();
    }

    public boolean isSet() {
        return countDownLatch.getCount() == 0;
    }

    @SneakyThrows
    public boolean await(final long timeoutMillis) {
        return countDownLatch.await(timeoutMillis, MILLISECONDS);
    }
}