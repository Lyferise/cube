package com.lyferise.cube.concurrency;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.BitSet;
import java.util.concurrent.CountDownLatch;

public class RingBufferStressTest {

    @Disabled
    @Test
    @SneakyThrows
    public void shouldReadAndWriteUsingMultipleThreads() {

        final int messageCount = 10000000;
        final RingBuffer<Integer> ringBuffer = new RingBuffer<>(1024);

        final CountDownLatch writers = new CountDownLatch(1);

        final BitSet all = new BitSet();
        final Thread writer = new Thread(() -> {
            for (int i = 0; i < messageCount; i++) {
                while (!ringBuffer.offer(i)) {
                    Thread.yield();
                }
            }
            writers.countDown();
        });

        final Thread reader = new Thread(() -> {
            while (!ringBuffer.isEmpty() && writers.getCount() > 0) {
                final Integer value = ringBuffer.poll();
                if (value != null) {
                    all.set(value);
                }
            }
        });

        writer.start();
        reader.start();

        writer.join();
        reader.join();

        System.out.println(all.cardinality());
        System.out.println(all.size());
    }
}