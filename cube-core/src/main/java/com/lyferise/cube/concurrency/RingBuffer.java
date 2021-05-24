package com.lyferise.cube.concurrency;

public class RingBuffer<T> {
    public static final int DEFAULT_CAPACITY = 16;

    private final int capacity;
    private final T[] buffer;
    private int readSequenceNumber;
    private int writeSequenceNumber = -1;

    public RingBuffer() {
        this(DEFAULT_CAPACITY);
    }

    @SuppressWarnings("unchecked")
    public RingBuffer(final int capacity) {
        this.capacity = capacity;
        this.buffer = (T[]) new Object[capacity];
    }

    public T next() {
        return buffer[readSequenceNumber++ % capacity];
    }

    public boolean offer(final T value) {
        if (isFull()) return false;
        buffer[(writeSequenceNumber++ + 1) % capacity] = value;
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