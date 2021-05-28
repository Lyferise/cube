package com.lyferise.cube.concurrency;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.max;
import static java.util.Arrays.fill;

public class RingBuffer<T> implements BlockingQueue<T> {
    public static final int DEFAULT_CAPACITY = 16;
    private final int capacity;
    private final T[] buffer;
    private final long mask;
    private long head;
    private long tail;

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
        return buffer[(int) (head & mask)];
    }

    @Override
    public T poll() {
        if (tail <= head) return null;
        final int index = (int) (head & mask);
        final T value = buffer[index];
        buffer[index] = null;
        head++;
        return value;
    }

    @Override
    public T poll(final long timeout, final TimeUnit unit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean offer(final T value) {
        if (isFull()) return false;
        buffer[(int) (tail++ & mask)] = value;
        return true;
    }

    @Override
    public boolean offer(final T t, final long timeout, final TimeUnit unit) {
        throw new UnsupportedOperationException();
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
        int count = 0;
        for (int i = 0; i < size(); i++) {
            final T data = buffer[(int) ((head + i) & mask)];
            if (data != null && data.equals(value)) {
                for (int j = i; j > 0; j--) {
                    buffer[(int) ((head + j) & mask)] = buffer[(int) ((head + j - 1) & mask)];
                }
                count++;
            }
        }
        if (count == 0) return false;
        head += count;
        return true;
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
    public void put(final T value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T take() {
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        fill(buffer, null);
        tail++;
        head = tail - 1;
    }

    @Override
    public boolean contains(final Object value) {
        for (int i = 0; i < size(); i++) {
            final T data = buffer[(int) ((head + i) & mask)];
            if (data != null && data.equals(value)) return true;
        }
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <V> V[] toArray(final V[] values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int drainTo(final Collection<? super T> values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int drainTo(final Collection<? super T> values, final int maxElements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return (int) max(tail - head, 0);
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isFull() {
        return head == tail - capacity;
    }

    @Override
    public boolean isEmpty() {
        return head == tail;
    }

    private static int getNextPowerOfTwo(final int value) {
        int k = 1;
        while (k < value) k <<= 1;
        return k;
    }
}