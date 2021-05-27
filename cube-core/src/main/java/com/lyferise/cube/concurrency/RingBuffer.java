package com.lyferise.cube.concurrency;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class RingBuffer<T> implements BlockingQueue<T> {
    public static final int DEFAULT_CAPACITY = 16;

    private final int capacity;
    private final T[] buffer;
    private volatile int readSequenceNumber;
    private volatile int writeSequenceNumber = -1;

    public RingBuffer() {
        this(DEFAULT_CAPACITY);
    }

    @SuppressWarnings("unchecked")
    public RingBuffer(final int capacity) {
        this.capacity = getNextPowerOfTwo(capacity);
        this.buffer = (T[]) new Object[this.capacity];
    }

    @Override
    public T poll() {
        if (isEmpty()) return null;
        final T nextValue = buffer[readSequenceNumber % capacity];
        readSequenceNumber++;
        return nextValue;
    }

    @Override
    public T element() {
        throw new UnsupportedOperationException();
    }

    @Override
    public T peek() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(final T value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean offer(final T value) {
        if (isFull()) return false;
        final int nextSequenceNumber = writeSequenceNumber + 1;
        buffer[nextSequenceNumber % capacity] = value;
        writeSequenceNumber++;
        return true;
    }

    @Override
    public T remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void put(final T value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean offer(final T t, final long timeout, final TimeUnit unit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T take() {
        throw new UnsupportedOperationException();
    }

    @Override
    public T poll(final long timeout, final TimeUnit unit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int remainingCapacity() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(final Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(final Collection<?> values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(final Collection<? extends T> values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(final Collection<?> values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(final Collection<?> values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(final Object value) {
        throw new UnsupportedOperationException();
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
        return (writeSequenceNumber - readSequenceNumber) + 1;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isFull() {
        return size() == capacity;
    }

    @Override
    public boolean isEmpty() {
        return writeSequenceNumber < readSequenceNumber;
    }

    private static int getNextPowerOfTwo(final int value) {
        int k = 1;
        while (k < value) k <<= 1;
        return k;
    }
}