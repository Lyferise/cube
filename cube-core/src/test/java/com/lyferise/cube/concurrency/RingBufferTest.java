package com.lyferise.cube.concurrency;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Disabled;
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

    @Disabled
    @Test
    @SneakyThrows
    public void shouldOfferThenTake() {

        // offer
        final int capacity = 100;
        final RingBuffer<Integer> ringBuffer = new RingBuffer<>(capacity);
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

    @Disabled
    @Test
    @SneakyThrows
    public void shouldPutThenPoll() {

        // offer
        final int capacity = 100;
        final RingBuffer<Integer> ringBuffer = new RingBuffer<>(capacity);
        for (int i = 0; i < capacity; i++) {
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
        final RingBuffer<Integer> ringBuffer = new RingBuffer<>(10);
        for (int i = 0; i < 100; i++) {
            ringBuffer.offer(i);
        }
        assertThat(ringBuffer.getCapacity(), is(equalTo(16)));
        assertThat(ringBuffer.size(), is(equalTo(16)));
    }

    @Test
    public void offerShouldFailWhenAtCapacity() {

        // capacity -> power of two
        final Map<Integer, Integer> map = Map.of(
                16, 16,
                42, 64,
                200, 256);

        // offer
        for (final int capacity : map.keySet()) {
            final int powerOfTwo = map.get(capacity);
            final RingBuffer<Integer> ringBuffer = new RingBuffer<>(capacity);
            for (int i = 0; i < capacity * 2; i++) {
                assertThat(ringBuffer.offer(i), is(equalTo(i < powerOfTwo)));
            }

            // full
            assertThat(ringBuffer.size(), is(equalTo(powerOfTwo)));
        }
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
    public void shouldGetRemainingCapacity() {
        final int capacity = 64;
        final RingBuffer<Integer> ringBuffer = new RingBuffer<>(capacity);
        for (int i = 0; i < capacity; i++) {
            assertThat(ringBuffer.remainingCapacity(), is(equalTo(capacity - i)));
            ringBuffer.offer(i);
        }
    }

    @Disabled
    @Test
    public void shouldPeekThenRemoveSingleItem() {

        // offer
        final int capacity = 100;
        final RingBuffer<Integer> ringBuffer = new RingBuffer<>(capacity);
        for (int i = 0; i < capacity; i++) {
            ringBuffer.offer(i);
        }

        // peek
        final Integer value = ringBuffer.peek();
        assertThat(value, is(equalTo(0)));

        // remove
        final Integer removed = ringBuffer.remove();
        assertThat(removed, is(equalTo(0)));
        assertThat(ringBuffer.peek(), is(equalTo(1)));
    }

    @Test
    public void shouldPeekThenPoll() {

        // peek
        final int capacity = 10;
        final RingBuffer<Integer> ringBuffer = new RingBuffer<>(capacity);
        assertThat(ringBuffer.peek(), is(nullValue()));

        // offer
        for (int i = 0; i < capacity; i++) {
            ringBuffer.offer(i);
        }

        // peek/poll
        for (int i = 0; i < capacity; i++) {
            assertThat(ringBuffer.peek(), is(equalTo(i)));
            ringBuffer.poll();
        }
    }

    @Test
    public void gettingHeadOfQueueShouldThrowWhenQueueIsEmpty() {
        final RingBuffer<Integer> ringBuffer = new RingBuffer<>();
        assertThrows(NoSuchElementException.class, ringBuffer::element);
    }

    @Disabled
    @Test
    public void shouldDrain() {

        // offer
        final int capacity = 100;
        final RingBuffer<Integer> ringBuffer = new RingBuffer<>(capacity);
        for (int i = 0; i < capacity; i++) {
            ringBuffer.offer(i);
        }

        // drain
        final List<Integer> values = new ArrayList<>();
        assertThat(ringBuffer.drainTo(values), is(equalTo(capacity)));
        for (int i = 0; i < capacity; i++) {
            assertThat(values.get(i), is(equalTo(i)));
        }
    }

    @Disabled
    @Test
    public void shouldDrainWithLimit() {

        // offer
        final int capacity = 100;
        final int limit = 75;
        final RingBuffer<Integer> ringBuffer = new RingBuffer<>(capacity);
        for (int i = 0; i < capacity; i++) {
            ringBuffer.offer(i);
        }

        // drain
        final List<Integer> values = new ArrayList<>();
        assertThat(ringBuffer.drainTo(values, limit), is(equalTo(limit)));
        assertThat(values.size(), is(equalTo(limit)));
        for (int i = 0; i < limit; i++) {
            assertThat(values.get(i), is(equalTo(i)));
        }
    }

    @Test
    public void shouldDetermineIfRingBufferIsEmpty() {

        // empty
        final int capacity = 100;
        final RingBuffer<Integer> ringBuffer = new RingBuffer<>(capacity);
        assertThat(ringBuffer.isEmpty(), is(true));

        for (int i = 0; i < 2; i++) {

            // offer
            for (int j = 0; j < capacity; j++) {
                ringBuffer.offer(j);
                assertThat(ringBuffer.isEmpty(), is(false));
            }

            // poll
            for (int j = 0; j < capacity; j++) {
                assertThat(ringBuffer.isEmpty(), is(false));
                ringBuffer.poll();
            }

            // empty
            assertThat(ringBuffer.isEmpty(), is(true));
        }
    }

    @Disabled
    @Test
    public void shouldDetermineIfRingBufferContainsValues() {

        // empty
        final int capacity = 100;
        final RingBuffer<Integer> ringBuffer = new RingBuffer<>(capacity);
        for (int i = 0; i < capacity; i++) {
            assertThat(ringBuffer.contains(i), is(false));
        }

        // offer
        for (int i = 0; i < capacity; i++) {
            assertThat(ringBuffer.offer(i), is(true));
        }

        // contains
        for (int i = 0; i < capacity; i++) {
            assertThat(ringBuffer.contains(i), is(true));
            assertThat(ringBuffer.contains(capacity + i), is(false));
        }
    }

    @Disabled
    @Test
    public void shouldDetermineIfRingBufferContainsCollection() {

        // offer
        final int capacity = 100;
        final RingBuffer<Integer> ringBuffer = new RingBuffer<>(capacity);
        for (int i = 0; i < capacity; i++) {
            ringBuffer.offer(i);
        }

        // subset
        final Set<Integer> subset = new HashSet<>(10);
        for (int i = 0; i < capacity / 10; i++) {
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
        final int capacity = 64;
        final RingBuffer<Integer> ringBuffer = new RingBuffer<>(capacity);
        for (int i = 0; i < capacity; i++) {
            assertThat(ringBuffer.add(i), is(true));
        }

        // full
        assertThrows(IllegalStateException.class, () -> ringBuffer.add(1));
    }

    @Disabled
    @Test
    public void shouldRemoveThenPollValues() {

        // offer
        final int capacity = 100;
        final RingBuffer<Integer> ringBuffer = new RingBuffer<>(capacity);
        for (int i = 0; i < capacity; i++) {
            ringBuffer.offer(i);
        }

        // remove even values
        for (int i = 0; i < capacity; i++) {
            if (i % 2 == 0) {
                ringBuffer.remove(i);
            }
        }
        assertThat(ringBuffer.size(), is(equalTo(capacity / 2)));

        // poll odd values
        for (int i = 0; i < capacity; i++) {
            if (i % 2 == 1) {
                assertThat(ringBuffer.poll(), is(equalTo(i)));
            }
        }
    }

    @Disabled
    @Test
    public void shouldRemoveDuplicates() {

        // offer
        final int capacity = 100;
        final RingBuffer<Integer> ringBuffer = new RingBuffer<>(capacity);
        for (int i = 0; i < capacity; i++) {
            ringBuffer.offer(i % 2);
        }

        // remove
        assertThat(ringBuffer.remove(42), is(equalTo(false)));
        assertThat(ringBuffer.size(), is(equalTo(capacity)));

        // remove
        ringBuffer.remove(1);
        assertThat(ringBuffer.size(), is(equalTo(capacity / 2)));

        // poll
        for (int i = 0; i < capacity / 2; i++) {
            assertThat(ringBuffer.poll(), is(equalTo(0)));
        }
    }

    @Disabled
    @Test
    public void shouldAddValuesThenPoll() {

        // add
        final int capacity = 64;
        final RingBuffer<Integer> ringBuffer = new RingBuffer<>(capacity);
        final Set<Integer> set = new HashSet<>();
        for (int i = 0; i < capacity; i++) {
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
        for (int i = 0; i < capacity * 2; i++) {
            set.add(i);
        }
        assertThat(ringBuffer.addAll(set), is(true));
        assertThat(ringBuffer.size(), is(capacity));
    }

    @Disabled
    @Test
    public void shouldAddValues() {

        // add
        final int capacity = 100;
        final RingBuffer<Integer> ringBuffer = new RingBuffer<>(capacity);

        // positive
        final Set<Integer> a = new HashSet<>(capacity);
        for (int i = 0; i < capacity / 10; i++) {
            a.add(i);
        }
        ringBuffer.addAll(a);
        assertThat(ringBuffer.containsAll(a), is(equalTo(true)));

        // negative
        final Set<Integer> b = new HashSet<>(capacity);
        for (int i = 0; i < capacity / 10; i++) {
            b.add(-i);
        }
        ringBuffer.addAll(b);
        assertThat(ringBuffer.size(), is(equalTo(2 * capacity / 10)));
        assertThat(ringBuffer.containsAll(a), is(equalTo(true)));
        assertThat(ringBuffer.containsAll(b), is(equalTo(true)));
    }

    @Disabled
    @Test
    public void shouldRemoveValues() {

        // positive
        final int capacity = 100;
        final RingBuffer<Integer> ringBuffer = new RingBuffer<>(capacity);
        final Set<Integer> positive = new HashSet<>(capacity);
        for (int i = 0; i < capacity / 10; i++) {
            positive.add(i);
        }
        ringBuffer.addAll(positive);
        assertThat(ringBuffer.containsAll(positive), is(equalTo(true)));

        // negative
        final Set<Integer> negative = new HashSet<>(capacity);
        for (int i = 1; i < capacity / 10; i++) {
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

    @Disabled
    @Test
    public void shouldRemoveEmptyCollection() {

        // add
        final int capacity = 64;
        final RingBuffer<Integer> ringBuffer = new RingBuffer<>(capacity);
        final Set<Integer> set = new HashSet<>();
        for (int i = 0; i < capacity; i++) {
            set.add(i);
        }
        ringBuffer.addAll(set);

        // remove
        final Collection<Integer> values = Collections.emptyList();
        assertThat(ringBuffer.removeAll(values), is(false));
        assertThat(ringBuffer.size(), is(equalTo(capacity)));
    }

    @Disabled
    @Test
    public void shouldRetainValues() {

        // offer
        final int capacity = 100;
        final RingBuffer<Integer> ringBuffer = new RingBuffer<>(capacity);
        final Set<Integer> values = new HashSet<>(capacity);
        for (int i = 0; i < capacity; i++) {
            ringBuffer.offer(i);
            values.add(i);
        }

        // retain
        final Set<Integer> subset = new HashSet<>(capacity);
        for (int i = 0; i < capacity / 10; i++) {
            subset.add(i);
            values.remove(i);
        }
        assertThat(ringBuffer.retainAll(subset), is(true));
        assertThat(ringBuffer.containsAll(subset), is(true));
        assertThat(ringBuffer.size(), is(equalTo(capacity / 10)));
        for (final int value : values) {
            assertThat(ringBuffer.contains(value), is(false));
        }
    }

    @Disabled
    @Test
    public void testRetainAllValues() {

        // offer
        final int capacity = 100;
        final RingBuffer<Integer> ringBuffer = new RingBuffer<>(capacity);
        final Set<Integer> values = new HashSet<>(capacity);
        for (int i = 0; i < capacity; i++) {
            values.add(i);
            ringBuffer.offer(i);
        }

        // retain
        assertThat(ringBuffer.retainAll(values), is(false));
        assertThat(ringBuffer.size(), is(equalTo(capacity)));
    }

    @Disabled
    @Test
    public void shouldClear() {

        // offer
        final int capacity = 100;
        final RingBuffer<Integer> ringBuffer = new RingBuffer<>(capacity);
        for (int i = 0; i < capacity; i++) {
            ringBuffer.offer(i);
        }

        // values
        final Set<Integer> values = new HashSet<>(capacity);
        for (int i = 0; i < capacity / 10; i++) {
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

    @Disabled
    @Test
    public void ringBufferShouldBeIterable() {

        // offer
        final int capacity = 100;
        final RingBuffer<Integer> ringBuffer = new RingBuffer<>(capacity);
        for (int i = 0; i < capacity; i++) {
            ringBuffer.offer(i);
        }

        // iterate
        int i = 0;
        for (final Integer value : ringBuffer) {
            assertThat(value, is(equalTo(i++)));
        }
    }

    @Disabled
    @Test
    public void shouldCopyRingBufferToArray() {

        // offer
        final int capacity = 100;
        final RingBuffer<Integer> ringBuffer = new RingBuffer<>(capacity);
        for (int i = 0; i < capacity; i++) {
            assertThat(ringBuffer.offer(i), is(true));
        }

        // copy
        final Object[] values = ringBuffer.toArray();
        for (int i = 0; i < capacity; i++) {
            assertThat(values[i], is(equalTo(i)));
        }
    }

    @Disabled
    @Test
    public void shouldCopyRingBufferToTypedArray() {

        // offer
        final int capacity = 100;
        final RingBuffer<Integer> ringBuffer = new RingBuffer<>(capacity);
        for (int i = 0; i < capacity; i++) {
            ringBuffer.offer(i);
        }

        // copy
        final Integer[] values = new Integer[capacity];
        ringBuffer.toArray(values);
        for (int i = 0; i < capacity; i++) {
            assertThat(values[i], is(equalTo(i)));
        }
    }
}