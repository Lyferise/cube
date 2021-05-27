package com.lyferise.cube.concurrency;

import org.junit.jupiter.api.Test;

import static com.lyferise.cube.concurrency.RingBuffer.DEFAULT_CAPACITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class RingBufferTest {

    @Test
    public void pollShouldReturnNullWhenRingBufferIsEmpty() {
        final RingBuffer<Integer> ringBuffer = new RingBuffer<>();
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
    public void shouldOfferThenPollTwoItems() {

        // empty
        final RingBuffer<Integer> ringBuffer = new RingBuffer<>();
        assertThat(ringBuffer.poll(), is(nullValue()));

        // offer
        assertThat(ringBuffer.offer(14), is(equalTo(true)));
        assertThat(ringBuffer.offer(15), is(equalTo(true)));

        // poll
        assertThat(ringBuffer.poll(), is(equalTo(14)));
        assertThat(ringBuffer.poll(), is(equalTo(15)));
        assertThat(ringBuffer.poll(), is(nullValue()));
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

    @Test
    public void offerShouldFailWhenAtCapacity() {

        // offer
        final int capacity = 16;
        final RingBuffer<Integer> ringBuffer = new RingBuffer<>(capacity);
        for (int i = 0; i < capacity * 2; i++) {
            assertThat(ringBuffer.offer(i), is(equalTo(i < capacity)));
        }

        // full
        assertThat(ringBuffer.size(), is(equalTo(capacity)));
    }

    @Test
    public void shouldGetSize() {

        // empty
        final int capacity = 100;
        final RingBuffer<Integer> ringBuffer = new RingBuffer<>(capacity);
        assertThat(ringBuffer.size(), is(equalTo(0)));

        for (int i = 0; i < 2; i++) {

            // offer
            for (int j = 0; j < capacity; j++) {
                assertThat(ringBuffer.offer(j), is(equalTo(true)));
                assertThat(ringBuffer.size(), is(equalTo(j + 1)));
            }
            assertThat(ringBuffer.size(), is(equalTo(capacity)));

            // poll
            for (int j = 0; j < capacity; j++) {
                assertThat(ringBuffer.size(), is(equalTo(capacity - j)));
                ringBuffer.poll();
            }
            assertThat(ringBuffer.size(), is(equalTo(0)));
        }
    }

    @Test
    public void capacityShouldBeAPowerOfTwo() {
        final RingBuffer<Integer> ringBuffer = new RingBuffer<>(10);
        for (int i = 0; i < 100; i++) {
            ringBuffer.offer(i);
        }
        assertThat(ringBuffer.getCapacity(), is(equalTo(16)));
        assertThat(ringBuffer.size(), is(equalTo(16)));
    }
}