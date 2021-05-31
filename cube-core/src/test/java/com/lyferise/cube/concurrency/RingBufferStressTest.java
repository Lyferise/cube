package com.lyferise.cube.concurrency;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.BitSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Runtime.getRuntime;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RingBufferStressTest {

    @Test
    @SneakyThrows
    public void shouldReadAndWriteUsingMultipleThreads() {

        // 10 million messages
        final var messageCount = 10_000_000;
        final var ringBuffer = new RingBuffer<Integer>(1024);

        // number of reader/writer threads
        final var threadCount = getRuntime().availableProcessors();

        // writers
        final var latch = new CountDownLatch(threadCount);
        final var writers = new Thread[threadCount];
        for (var i = 0; i < threadCount; i++) {
            final var k = i;
            writers[i] = new Thread(() -> {
                for (var j = k; j < messageCount; j += threadCount) {
                    while (!ringBuffer.offer(j)) {
                        Thread.yield();
                    }
                }
                latch.countDown();
            });
        }

        final var bitSet = new BitSet();

        // readers
        final var readers = new Thread[threadCount];
        final var lock = new ReentrantLock();
        for (var i = 0; i < threadCount; i++) {
            readers[i] = new Thread(() -> {
                final var readSet = new BitSet();
                while (!ringBuffer.isEmpty() || latch.getCount() > 0) {
                    final Integer value = ringBuffer.poll();
                    if (value != null) {
                        readSet.set(value);
                    } else {
                        Thread.yield();
                    }
                }

                lock.lock();
                try {
                    bitSet.or(readSet);
                } finally {
                    lock.unlock();
                }
            });
        }

        // start threads
        for (var i = 0; i < threadCount; i++) {
            writers[i].start();
            readers[i].start();
        }

        // wait for threads to terminate
        for (var i = 0; i < threadCount; i++) {
            writers[i].join();
            readers[i].join();
        }

        // verify messages seen by readers
        assertEquals(messageCount, bitSet.cardinality());
        assertEquals(messageCount, bitSet.length());
    }
}