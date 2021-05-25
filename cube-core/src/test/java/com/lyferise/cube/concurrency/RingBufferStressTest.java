package com.lyferise.cube.concurrency;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.BitSet;
import java.util.concurrent.CountDownLatch;

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

        // writers
        final CountDownLatch writers = new CountDownLatch(1);
        final Thread writer = new Thread(() -> {
            for (int i = 0; i < messageCount; i++) {
                while (!ringBuffer.offer(i)) {
                    Thread.yield();
                }
            }
            writers.countDown();
        });

        // readers
        final BitSet bitSet = new BitSet();
        final Thread reader = new Thread(() -> {
            while (!ringBuffer.isEmpty() || writers.getCount() > 0) {
                final Integer value = ringBuffer.poll();
                if (value != null) {
                    bitSet.set(value);
                }
            }
        });

        // start threads
        writer.start();
        reader.start();

        // wait for threads to terminate
        writer.join();
        reader.join();

        // verify messages seen by readers
        assertThat(bitSet.cardinality(), is(equalTo(messageCount)));
        assertThat(bitSet.length(), is(equalTo(messageCount)));
    }
}