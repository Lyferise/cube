package com.lyferise.cube.concurrency;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.System.nanoTime;
import static java.lang.Thread.currentThread;
import static java.util.Arrays.fill;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

public class RingBuffer<T> implements BlockingQueue<T> {
    public static final int DEFAULT_CAPACITY = 16;

    private static final long WAIT_TIMEOUT_NANOS = 50;
    private final int capacity;
    private final T[] buffer;
    private final long mask;
    private final LongAdder head = new LongAdder();
    private final LongAdder tail = new LongAdder();
    private long headCache = 0;
    private long tailCache = 0;
    private final CacheLineValue headCursor = new CacheLineValue();
    private final CacheLineValue tailCursor = new CacheLineValue();
    private final QueueCondition notEmpty = new NotEmpty();
    private final QueueCondition notFull = new NotFull();

    public RingBuffer() {
        this(DEFAULT_CAPACITY);
    }

    @SuppressWarnings("unchecked")
    public RingBuffer(final int capacity) {
        this.capacity = getNextPowerOfTwo(capacity);
        this.mask = this.capacity - 1;
        this.buffer = (T[]) new Object[this.capacity];
    }

    @Override
    public T peek() {
        return buffer[(int) (head.sum() & mask)];
    }

    @Override
    public T poll() {
        while (true) {
            final long head = this.head.sum();
            if (tailCache <= head && (tailCache = tail.sum()) <= head) return null;
            if (headCursor.compareAndSet(head, head + 1)) {
                final int index = (int) (head & mask);
                final T value = buffer[index];
                buffer[index] = null;
                this.head.increment();
                notFull.signal();
                return value;
            }
        }
    }

    @Override
    public T poll(final long timeout, final TimeUnit unit) throws InterruptedException {
        while (true) {
            final T value = poll();
            if (value != null) return value;
            if (timedOutFor(notEmpty, timeout, unit)) return null;
        }
    }

    @Override
    public boolean offer(final T value) {
        while (true) {
            final long tail = this.tail.sum();
            final long index = tail - capacity;
            if (headCache <= index && (headCache = head.sum()) <= index) {
                notEmpty.signal();
                return false;
            }

            if (tailCursor.compareAndSet(tail, tail + 1)) {
                buffer[(int) (tail & mask)] = value;
                this.tail.increment();
                notEmpty.signal();
                return true;
            }
        }
    }

    @Override
    public boolean offer(final T value, final long timeout, final TimeUnit unit) throws InterruptedException {
        while (true) {
            if (offer(value)) return true;
            if (timedOutFor(notFull, timeout, unit)) return false;
        }
    }

    @Override
    public T element() {
        final T value = peek();
        if (value == null) throw new NoSuchElementException();
        return value;
    }

    @Override
    public boolean add(final T value) {
        if (offer(value)) return true;
        throw new IllegalStateException("ring buffer is full");
    }

    @Override
    public boolean addAll(final Collection<? extends T> values) {
        boolean modified = false;
        for (final T value : values) {
            modified |= offer(value);
        }
        return modified;
    }

    @Override
    public T remove() {
        return poll();
    }

    @Override
    public boolean remove(final Object value) {
        while (true) {
            final long head = this.head.sum();
            if (headCursor.compareAndSet(head, head + 1)) {
                while (true) {
                    final long tail = this.tail.sum();
                    if (tailCursor.compareAndSet(tail, tail + 1)) {
                        int count = 0;
                        for (int i = 0; i < size(); i++) {
                            final T data = buffer[(int) ((this.head.sum() + i) & mask)];
                            if (data != null && data.equals(value)) {
                                count++;
                                for (int j = i; j > 0; j--) {
                                    buffer[(int) ((this.head.sum() + j) & mask)] = buffer[(int) ((this.head.sum() + j - 1) & mask)];
                                }
                            }
                        }

                        if (count > 0) {
                            headCursor.set(head + count);
                            tailCursor.set(tail);
                            this.head.add(count);
                            notFull.signal();
                            return true;
                        }

                        tailCursor.set(tail);
                        headCursor.set(head);
                        return false;
                    }
                }
            }
        }
    }

    @Override
    public boolean removeAll(final Collection<?> values) {
        boolean modified = false;
        for (final Object value : values) {
            modified |= remove(value);
        }
        return modified;
    }

    @Override
    public void put(final T value) throws InterruptedException {
        while (!offer(value)) {
            if (currentThread().isInterrupted()) throw new InterruptedException();
            notFull.await();
        }
    }

    @Override
    public T take() throws InterruptedException {
        while (true) {
            final T value = poll();
            if (value != null) return value;
            if (currentThread().isInterrupted()) throw new InterruptedException();
            notEmpty.await();
        }
    }

    @Override
    public int remainingCapacity() {
        return capacity - size();
    }

    @Override
    public boolean containsAll(final Collection<?> values) {
        for (final Object value : values) {
            if (!contains(value)) return false;
        }
        return true;
    }

    @Override
    public boolean retainAll(final Collection<?> values) {
        boolean modified = false;
        for (int i = 0; i < size(); i++) {
            final int index = (int) ((head.sum() + i) & mask);
            final T value = buffer[index];
            if (value != null && !values.contains(value) && remove(buffer[index])) {
                modified = true;
                i--;
            }
        }
        return modified;
    }

    @Override
    public void clear() {
        while (true) {
            final long head = this.head.sum();
            if (!headCursor.compareAndSet(head, head + 1)) continue;

            while (true) {
                final long tail = this.tail.sum();
                if (tailCursor.compareAndSet(tail, tail + 1)) {
                    fill(buffer, null);
                    this.tail.increment();
                    this.head.add(tail - head + 1);
                    headCursor.set(tail + 1);
                    notFull.signal();
                    return;
                }
            }
        }
    }

    @Override
    public boolean contains(final Object value) {
        for (int i = 0; i < size(); i++) {
            final T data = buffer[(int) ((head.sum() + i) & mask)];
            if (data != null && data.equals(value)) return true;
        }
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        return new RingBufferIterator();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object[] toArray() {
        final T[] values = (T[]) new Object[size()];
        toArray(values);
        return values;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> V[] toArray(final V[] values) {
        moveTo((T[]) values);
        return values;
    }

    @Override
    public int drainTo(final Collection<? super T> values) {
        return drainTo(values, size());
    }

    @SuppressWarnings("unchecked")
    @Override
    public int drainTo(final Collection<? super T> values, final int maxElements) {

        // move
        if (this == values) throw new IllegalArgumentException("Can't drain ring buffer to same instance.");
        final T[] data = (T[]) new Object[min(size(), maxElements)];
        final int moved = moveTo(data);

        // add
        int count = 0;
        for (int i = 0; i < moved; i++) {
            if (values.add(data[i])) count++;
        }
        return count;
    }

    public int moveTo(final T[] values) {
        final int length = values.length;
        while (true) {

            // count
            final long head = this.head.sum();
            final int count = min((int) (this.tail.sum() - head), length);
            if (count <= 0) return 0;

            // values
            for (int i = 0; i < count; i++) {
                values[i] = buffer[(int) ((head + i) & mask)];
            }

            // done?
            if (headCursor.compareAndSet(head, head + count)) {
                this.head.add(count);
                notFull.signal();
                return count;
            }
        }
    }

    @Override
    public final int size() {
        return (int) max(tail.sum() - head.sum(), 0);
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isFull() {
        return head.sum() == tail.sum() - capacity;
    }

    @Override
    public final boolean isEmpty() {
        return head.sum() == tail.sum();
    }

    private static int getNextPowerOfTwo(final int value) {
        int k = 1;
        while (k < value) k <<= 1;
        return k;
    }

    private static boolean timedOutFor(
            final QueueCondition condition, final long timeout, final TimeUnit unit)
            throws InterruptedException {

        final long t1 = nanoTime() + NANOSECONDS.convert(timeout, unit);
        while (condition.test()) {
            final long remaining = t1 - nanoTime();
            if (remaining <= 0) return true;
            condition.awaitNanos(remaining - WAIT_TIMEOUT_NANOS);
        }
        return false;
    }

    private final class NotEmpty extends QueueCondition {

        @Override
        public final boolean test() {
            return isEmpty();
        }
    }

    private final class NotFull extends QueueCondition {

        @Override
        public final boolean test() {
            return isFull();
        }
    }

    private final class RingBufferIterator implements Iterator<T> {
        private int index;
        private T value;

        @Override
        public boolean hasNext() {
            return index < size();
        }

        @Override
        public T next() {
            return value = buffer[(int) ((head.sum() + index++) & mask)];
        }

        @Override
        public void remove() {
            RingBuffer.this.remove(value);
        }
    }
}