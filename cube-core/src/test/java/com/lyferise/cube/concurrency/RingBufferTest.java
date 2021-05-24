package com.lyferise.cube.concurrency;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.lyferise.cube.concurrency.RingBuffer.DEFAULT_CAPACITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class RingBufferTest {

    @Test
    public void pollShouldReturnNullWhenRingBufferIsEmpty() {
        final RingBuffer<Integer> ringBuffer = new RingBuffer<>();
        assertThat(ringBuffer.isEmpty(), is(equalTo(true)));
        assertThat(ringBuffer.poll(), is(nullValue()));
    }

    @Test
    public void shouldOfferThenPollSingleItem() {

        // empty
        final RingBuffer<Integer> ringBuffer = new RingBuffer<>();
        assertThat(ringBuffer.getCapacity(), is(equalTo(DEFAULT_CAPACITY)));
        assertThat(ringBuffer.size(), is(equalTo(0)));
        assertThat(ringBuffer.isEmpty(), is(equalTo(true)));
        assertThat(ringBuffer.isFull(), is(equalTo(false)));

        // offer
        assertThat(ringBuffer.offer(5), is(equalTo(true)));
        assertThat(ringBuffer.size(), is(equalTo(1)));
        assertThat(ringBuffer.isEmpty(), is(equalTo(false)));
        assertThat(ringBuffer.isFull(), is(equalTo(false)));

        // poll
        assertThat(ringBuffer.poll(), is(equalTo(5)));
    }

    @Test
    public void shouldOfferUntilFull() {

        // offer
        final int capacity = 100;
        final RingBuffer<Integer> ringBuffer = new RingBuffer<>(capacity);
        for (int i = 0; i < capacity; i++) {
            assertThat(ringBuffer.offer(i), is(equalTo(true)));
        }

        // poll
        for (int i = 0; i < capacity; i++) {
            assertThat(ringBuffer.poll(), is(equalTo(i)));
        }
        assertThat(ringBuffer.poll(), is(nullValue()));
    }
}