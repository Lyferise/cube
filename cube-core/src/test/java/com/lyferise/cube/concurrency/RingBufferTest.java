package com.lyferise.cube.concurrency;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.*;

import static com.lyferise.cube.concurrency.RingBuffer.DEFAULT_CAPACITY;
import static java.lang.Thread.sleep;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RingBufferTest {

    @Test
    public void pollShouldReturnNullWhenRingBufferIsEmpty() {
        final var ringBuffer = new RingBuffer<Integer>();
        assertThat(ringBuffer.poll(), is(nullValue()));
    }

    @Test
    public void shouldOfferThenPollSingleItem() {

        // empty
        final var ringBuffer = new RingBuffer<Integer>();
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
        final var ringBuffer = new RingBuffer<Integer>();
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
        final var capacity = 100;
        final var ringBuffer = new RingBuffer<Integer>(capacity);
        for (var i = 0; i < capacity; i++) {
            assertThat(ringBuffer.offer(i), is(equalTo(true)));
        }

        // poll
        for (var i = 0; i < capacity; i++) {
            assertThat(ringBuffer.poll(), is(equalTo(i)));
        }
        assertThat(ringBuffer.poll(), is(nullValue()));
    }

    @Test
    @SneakyThrows
    public void shouldOfferThenTake() {

        // offer
        final var capacity = 100;
        final var ringBuffer = new RingBuffer<Integer>(capacity);
        new Thread(() -> {
            try {
                sleep(1000);
                ringBuffer.offer(capacity);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        // take
        assertThat(ringBuffer.take(), is(equalTo(capacity)));
    }

    @Test
    @SneakyThrows
    public void shouldPutThenPoll() {

        // offer
        final var capacity = 100;
        final var ringBuffer = new RingBuffer<Integer>(capacity);
        for (var i = 0; i < capacity; i++) {
            ringBuffer.offer(i);
        }

        // poll
        new Thread(() -> {
            try {
                sleep(1000);
                ringBuffer.poll();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        // put
        ringBuffer.put(capacity);
        Integer head = null;
        while (!ringBuffer.isEmpty()) {
            head = ringBuffer.poll();
        }
        assertThat(head, is(equalTo(capacity)));
    }

    @Test
    public void capacityShouldBeAPowerOfTwo() {
        final var ringBuffer = new RingBuffer<Integer>(10);
        for (var i = 0; i < 100; i++) {
            ringBuffer.offer(i);
        }
        assertThat(ringBuffer.getCapacity(), is(equalTo(16)));
        assertThat(ringBuffer.size(), is(equalTo(16)));
    }

    @Test
    public void offerShouldFailWhenAtCapacity() {

        // capacity -> power of two
        final var map = Map.of(
                16, 16,
                42, 64,
                200, 256);

        // offer
        for (final var capacity : map.keySet()) {
            final var powerOfTwo = map.get(capacity);
            final var ringBuffer = new RingBuffer<Integer>(capacity);
            for (var i = 0; i < capacity * 2; i++) {
                assertThat(ringBuffer.offer(i), is(equalTo(i < powerOfTwo)));
            }

            // full
            assertThat(ringBuffer.size(), is(equalTo(powerOfTwo)));
        }
    }

    @Test
    public void shouldGetSize() {

        // empty
        final var capacity = 100;
        final var ringBuffer = new RingBuffer<Integer>(capacity);
        assertThat(ringBuffer.size(), is(equalTo(0)));

        for (var i = 0; i < 2; i++) {

            // offer
            for (var j = 0; j < capacity; j++) {
                assertThat(ringBuffer.offer(j), is(equalTo(true)));
                assertThat(ringBuffer.size(), is(equalTo(j + 1)));
            }
            assertThat(ringBuffer.size(), is(equalTo(capacity)));

            // poll
            for (var j = 0; j < capacity; j++) {
                assertThat(ringBuffer.size(), is(equalTo(capacity - j)));
                ringBuffer.poll();
            }
            assertThat(ringBuffer.size(), is(equalTo(0)));
        }
    }

    @Test
    public void shouldGetRemainingCapacity() {
        final var capacity = 64;
        final var ringBuffer = new RingBuffer<Integer>(capacity);
        for (var i = 0; i < capacity; i++) {
            assertThat(ringBuffer.remainingCapacity(), is(equalTo(capacity - i)));
            ringBuffer.offer(i);
        }
    }

    @Test
    public void shouldPeekThenRemoveSingleItem() {

        // offer
        final var capacity = 100;
        final var ringBuffer = new RingBuffer<Integer>(capacity);
        for (var i = 0; i < capacity; i++) {
            ringBuffer.offer(i);
        }

        // peek
        final var value = ringBuffer.peek();
        assertThat(value, is(equalTo(0)));

        // remove
        final var removed = ringBuffer.remove();
        assertThat(removed, is(equalTo(0)));
        assertThat(ringBuffer.peek(), is(equalTo(1)));
    }

    @Test
    public void shouldPeekThenPoll() {

        // peek
        final var capacity = 10;
        final var ringBuffer = new RingBuffer<Integer>(capacity);
        assertThat(ringBuffer.peek(), is(nullValue()));

        // offer
        for (var i = 0; i < capacity; i++) {
            ringBuffer.offer(i);
        }

        // peek/poll
        for (var i = 0; i < capacity; i++) {
            assertThat(ringBuffer.peek(), is(equalTo(i)));
            ringBuffer.poll();
        }
    }

    @Test
    public void gettingHeadOfQueueShouldThrowWhenQueueIsEmpty() {
        final var ringBuffer = new RingBuffer<Integer>();
        assertThrows(NoSuchElementException.class, ringBuffer::element);
    }

    @Test
    public void shouldDrain() {

        // offer
        final var capacity = 100;
        final var ringBuffer = new RingBuffer<Integer>(capacity);
        for (var i = 0; i < capacity; i++) {
            ringBuffer.offer(i);
        }

        // drain
        final var values = new ArrayList<Integer>();
        assertThat(ringBuffer.drainTo(values), is(equalTo(capacity)));
        for (var i = 0; i < capacity; i++) {
            assertThat(values.get(i), is(equalTo(i)));
        }
    }

    @Test
    public void shouldDrainWithLimit() {

        // offer
        final var capacity = 100;
        final var limit = 75;
        final var ringBuffer = new RingBuffer<Integer>(capacity);
        for (var i = 0; i < capacity; i++) {
            ringBuffer.offer(i);
        }

        // drain
        final var values = new ArrayList<Integer>();
        assertThat(ringBuffer.drainTo(values, limit), is(equalTo(limit)));
        assertThat(values.size(), is(equalTo(limit)));
        for (var i = 0; i < limit; i++) {
            assertThat(values.get(i), is(equalTo(i)));
        }
    }

    @Test
    public void shouldDetermineIfRingBufferIsEmpty() {

        // empty
        final var capacity = 100;
        final var ringBuffer = new RingBuffer<Integer>(capacity);
        assertThat(ringBuffer.isEmpty(), is(true));

        for (var i = 0; i < 2; i++) {

            // offer
            for (var j = 0; j < capacity; j++) {
                ringBuffer.offer(j);
                assertThat(ringBuffer.isEmpty(), is(false));
            }

            // poll
            for (var j = 0; j < capacity; j++) {
                assertThat(ringBuffer.isEmpty(), is(false));
                ringBuffer.poll();
            }

            // empty
            assertThat(ringBuffer.isEmpty(), is(true));
        }
    }

    @Test
    public void shouldDetermineIfRingBufferContainsValues() {

        // empty
        final var capacity = 100;
        final var ringBuffer = new RingBuffer<Integer>(capacity);
        for (var i = 0; i < capacity; i++) {
            assertThat(ringBuffer.contains(i), is(false));
        }

        // offer
        for (var i = 0; i < capacity; i++) {
            assertThat(ringBuffer.offer(i), is(true));
        }

        // contains
        for (var i = 0; i < capacity; i++) {
            assertThat(ringBuffer.contains(i), is(true));
            assertThat(ringBuffer.contains(capacity + i), is(false));
        }
    }

    @Test
    public void shouldDetermineIfRingBufferContainsCollection() {

        // offer
        final var capacity = 100;
        final var ringBuffer = new RingBuffer<Integer>(capacity);
        for (var i = 0; i < capacity; i++) {
            ringBuffer.offer(i);
        }

        // subset
        final var subset = new HashSet<Integer>(10);
        for (var i = 0; i < capacity / 10; i++) {
            subset.add(i);
        }
        assertThat(ringBuffer.containsAll(subset), is(equalTo(true)));

        // add
        subset.add(-1);
        assertThat(ringBuffer.containsAll(subset), is(equalTo(false)));

        // remove
        subset.remove(-1);
        ringBuffer.clear();
        assertThat(ringBuffer.containsAll(subset), is(equalTo(false)));
    }

    @Test
    public void addShouldThrowWhenAtCapacity() {

        // add
        final var capacity = 64;
        final var ringBuffer = new RingBuffer<Integer>(capacity);
        for (var i = 0; i < capacity; i++) {
            assertThat(ringBuffer.add(i), is(true));
        }

        // full
        assertThrows(IllegalStateException.class, () -> ringBuffer.add(1));
    }

    @Test
    public void shouldRemoveThenPollValues() {

        // offer
        final var capacity = 100;
        final var ringBuffer = new RingBuffer<Integer>(capacity);
        for (var i = 0; i < capacity; i++) {
            ringBuffer.offer(i);
        }

        // remove even values
        for (var i = 0; i < capacity; i++) {
            if (i % 2 == 0) {
                ringBuffer.remove(i);
            }
        }
        assertThat(ringBuffer.size(), is(equalTo(capacity / 2)));

        // poll odd values
        for (var i = 0; i < capacity; i++) {
            if (i % 2 == 1) {
                assertThat(ringBuffer.poll(), is(equalTo(i)));
            }
        }
    }

    @Test
    public void shouldRemoveDuplicates() {

        // offer
        final var capacity = 100;
        final var ringBuffer = new RingBuffer<Integer>(capacity);
        for (var i = 0; i < capacity; i++) {
            ringBuffer.offer(i % 2);
        }

        // remove
        assertThat(ringBuffer.remove(42), is(equalTo(false)));
        assertThat(ringBuffer.size(), is(equalTo(capacity)));

        // remove
        ringBuffer.remove(1);
        assertThat(ringBuffer.size(), is(equalTo(capacity / 2)));

        // poll
        for (var i = 0; i < capacity / 2; i++) {
            assertThat(ringBuffer.poll(), is(equalTo(0)));
        }
    }

    @Test
    public void shouldAddValuesThenPoll() {

        // add
        final var capacity = 64;
        final var ringBuffer = new RingBuffer<Integer>(capacity);
        final var set = new HashSet<Integer>();
        for (var i = 0; i < capacity; i++) {
            set.add(i);
        }
        assertThat(ringBuffer.addAll(set), is(true));
        assertThat(ringBuffer.size(), is(capacity));

        // poll
        Integer value = ringBuffer.poll();
        while (value != null) {
            assertThat(set.contains(value), is(true));
            value = ringBuffer.poll();
        }
        assertThat(ringBuffer.isEmpty(), is(true));

        // add
        for (var i = 0; i < capacity * 2; i++) {
            set.add(i);
        }
        assertThat(ringBuffer.addAll(set), is(true));
        assertThat(ringBuffer.size(), is(capacity));
    }

    @Test
    public void shouldAddValues() {

        // add
        final var capacity = 100;
        final var ringBuffer = new RingBuffer<Integer>(capacity);

        // positive
        final var a = new HashSet<Integer>(capacity);
        for (var i = 0; i < capacity / 10; i++) {
            a.add(i);
        }
        ringBuffer.addAll(a);
        assertThat(ringBuffer.containsAll(a), is(equalTo(true)));

        // negative
        final var b = new HashSet<Integer>(capacity);
        for (var i = 0; i < capacity / 10; i++) {
            b.add(-i);
        }
        ringBuffer.addAll(b);
        assertThat(ringBuffer.size(), is(equalTo(2 * capacity / 10)));
        assertThat(ringBuffer.containsAll(a), is(equalTo(true)));
        assertThat(ringBuffer.containsAll(b), is(equalTo(true)));
    }

    @Test
    public void shouldRemoveValues() {

        // positive
        final var capacity = 100;
        final var ringBuffer = new RingBuffer<Integer>(capacity);
        final var positive = new HashSet<Integer>(capacity);
        for (var i = 0; i < capacity / 10; i++) {
            positive.add(i);
        }
        ringBuffer.addAll(positive);
        assertThat(ringBuffer.containsAll(positive), is(equalTo(true)));

        // negative
        final var negative = new HashSet<Integer>(capacity);
        for (var i = 1; i < capacity / 10; i++) {
            negative.add(-i);
        }
        ringBuffer.addAll(negative);
        assertThat(ringBuffer.containsAll(positive), is(equalTo(true)));
        assertThat(ringBuffer.containsAll(negative), is(equalTo(true)));

        // remove
        assertThat(ringBuffer.removeAll(positive), is(equalTo(true)));
        assertThat(ringBuffer.containsAll(negative), is(equalTo(true)));
        assertThat(ringBuffer.containsAll(positive), is(equalTo(false)));

        // remove
        assertThat(ringBuffer.removeAll(negative), is(equalTo(true)));
        assertThat(ringBuffer.containsAll(negative), is(equalTo(false)));
        assertThat(ringBuffer.containsAll(positive), is(equalTo(false)));
    }

    @Test
    public void shouldRemoveEmptyCollection() {

        // add
        final var capacity = 64;
        final var ringBuffer = new RingBuffer<Integer>(capacity);
        final var set = new HashSet<Integer>();
        for (var i = 0; i < capacity; i++) {
            set.add(i);
        }
        ringBuffer.addAll(set);

        // remove
        final Collection<Integer> values = Collections.emptyList();
        assertThat(ringBuffer.removeAll(values), is(false));
        assertThat(ringBuffer.size(), is(equalTo(capacity)));
    }

    @Test
    public void shouldRetainValues() {

        // offer
        final var capacity = 100;
        final var ringBuffer = new RingBuffer<Integer>(capacity);
        final var values = new HashSet<Integer>(capacity);
        for (var i = 0; i < capacity; i++) {
            ringBuffer.offer(i);
            values.add(i);
        }

        // retain
        final var subset = new HashSet<Integer>(capacity);
        for (var i = 0; i < capacity / 10; i++) {
            subset.add(i);
            values.remove(i);
        }
        assertThat(ringBuffer.retainAll(subset), is(true));
        assertThat(ringBuffer.containsAll(subset), is(true));
        assertThat(ringBuffer.size(), is(equalTo(capacity / 10)));
        for (final var value : values) {
            assertThat(ringBuffer.contains(value), is(false));
        }
    }

    @Test
    public void shouldRetainAllValues() {

        // offer
        final var capacity = 100;
        final var ringBuffer = new RingBuffer<Integer>(capacity);
        final var values = new HashSet<Integer>(capacity);
        for (var i = 0; i < capacity; i++) {
            values.add(i);
            ringBuffer.offer(i);
        }

        // retain
        assertThat(ringBuffer.retainAll(values), is(false));
        assertThat(ringBuffer.size(), is(equalTo(capacity)));
    }

    @Test
    public void shouldClear() {

        // offer
        final var capacity = 100;
        final var ringBuffer = new RingBuffer<Integer>(capacity);
        for (var i = 0; i < capacity; i++) {
            ringBuffer.offer(i);
        }

        // values
        final var values = new HashSet<Integer>(capacity);
        for (var i = 0; i < capacity / 10; i++) {
            values.add(i);
        }
        assertThat(ringBuffer.containsAll(values), is(true));

        // clear
        ringBuffer.clear();

        // empty
        assertThat(ringBuffer.containsAll(values), is(equalTo(false)));
        assertThat(ringBuffer.poll(), is(nullValue()));
        assertThat(ringBuffer.isEmpty(), is(true));
        assertThat(ringBuffer.size(), is(equalTo(0)));
    }

    @Test
    public void ringBufferShouldBeIterable() {

        // offer
        final var capacity = 100;
        final var ringBuffer = new RingBuffer<Integer>(capacity);
        for (var i = 0; i < capacity; i++) {
            ringBuffer.offer(i);
        }

        // iterate
        int i = 0;
        for (final Integer value : ringBuffer) {
            assertThat(value, is(equalTo(i++)));
        }
    }

    @Test
    public void shouldCopyRingBufferToArray() {

        // offer
        final var capacity = 100;
        final var ringBuffer = new RingBuffer<Integer>(capacity);
        for (var i = 0; i < capacity; i++) {
            assertThat(ringBuffer.offer(i), is(true));
        }

        // copy
        final var values = ringBuffer.toArray();
        for (var i = 0; i < capacity; i++) {
            assertThat(values[i], is(equalTo(i)));
        }
    }

    @Test
    public void shouldCopyRingBufferToTypedArray() {

        // offer
        final var capacity = 100;
        final var ringBuffer = new RingBuffer<Integer>(capacity);
        for (var i = 0; i < capacity; i++) {
            ringBuffer.offer(i);
        }

        // copy
        final var values = new Integer[capacity];
        ringBuffer.toArray(values);
        for (var i = 0; i < capacity; i++) {
            assertThat(values[i], is(equalTo(i)));
        }
    }
}