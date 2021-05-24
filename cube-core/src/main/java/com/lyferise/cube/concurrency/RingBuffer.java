package com.lyferise.cube.concurrency;

public class RingBuffer<T> {
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
        this.capacity = capacity;
        this.buffer = (T[]) new Object[capacity];
    }

    public T poll() {
        if (isEmpty()) return null;
        final T nextValue = buffer[readSequenceNumber % capacity];
        readSequenceNumber++;
        return nextValue;
    }

    public boolean offer(final T value) {
        if (isFull()) return false;
        final int nextSequenceNumber = writeSequenceNumber + 1;
        buffer[nextSequenceNumber % capacity] = value;
        writeSequenceNumber++;
        return true;
    }

    public int size() {
        return (writeSequenceNumber - readSequenceNumber) + 1;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isFull() {
        return size() == capacity;
    }

    public boolean isEmpty() {
        return writeSequenceNumber < readSequenceNumber;
    }
}