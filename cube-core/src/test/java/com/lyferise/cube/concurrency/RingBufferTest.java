package com.lyferise.cube.concurrency;

import org.junit.jupiter.api.Test;

import static com.lyferise.cube.concurrency.RingBuffer.DEFAULT_CAPACITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class RingBufferTest {

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
}