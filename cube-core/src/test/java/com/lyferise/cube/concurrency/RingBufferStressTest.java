package com.lyferise.cube.concurrency;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.BitSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class RingBufferStressTest {

    @Test
    @SneakyThrows
    public void shouldReadAndWriteUsingMultipleThreads() {

        // 10 million messages
        final int messageCount = 10_000_000;
        final RingBuffer<Integer> ringBuffer = new RingBuffer<>(1024);

        // number of reader/writer threads
        final int threadCount = 1;

        // writers
        final CountDownLatch latch = new CountDownLatch(threadCount);
        final Thread[] writers = new Thread[threadCount];
        final AtomicInteger w = new AtomicInteger();
        for (int i = 0; i < threadCount; i++) {
            writers[i] = new Thread(() -> {
                for (int j = 0; j < messageCount; j++) {
                    while (!ringBuffer.offer(j)) {
                        Thread.yield();
                    }
                    w.incrementAndGet();
                }
                latch.countDown();
            });
        }

        final BitSet bitSet = new BitSet();

        // readers
        final AtomicInteger r = new AtomicInteger();
        final Thread reader = new Thread(() -> {
            while (!ringBuffer.isEmpty() || latch.getCount() > 0) {
                final Integer value = ringBuffer.poll();
                if (value != null) {
                    bitSet.set(value);
                    r.incrementAndGet();
                }
            }
        });

        // start threads
        for (int i = 0; i < threadCount; i++) {
            writers[i].start();
        }
        reader.start();

        // wait for threads to terminate
        for (int i = 0; i < threadCount; i++) {
            writers[i].join();
        }
        reader.join();
        System.err.println("w = " + w);
        System.err.println("r = " + r);

        // verify messages seen by readers
        System.err.println("cardinality = " + bitSet.cardinality());
        System.err.println("length = " + bitSet.length());
        assertThat(bitSet.cardinality(), is(equalTo(messageCount)));
        assertThat(bitSet.length(), is(equalTo(messageCount)));
    }
}