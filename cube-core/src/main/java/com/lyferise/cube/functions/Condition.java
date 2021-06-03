package com.lyferise.cube.functions;

import lombok.SneakyThrows;

import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.sleep;

@FunctionalInterface
public interface Condition {

    boolean test();

    @SneakyThrows
    static void waitFor(final Condition condition) {
        final long start = currentTimeMillis();

        while (!condition.test()) {
            final var elapsed = currentTimeMillis() - start;
            if (elapsed > 5000) {
                throw new UnsupportedOperationException("Wait condition timeout after " + elapsed + " ms.");
            }
            sleep(50);
        }
    }
}