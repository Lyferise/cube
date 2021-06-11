package com.lyferise.cube.collections;

public class CircularBuffer<T> {
    private static final int DEFAULT_CAPACITY = 16;
    private final T[] buffer;
    private final int capacity;
    private int head;
    private int tail;
    private int size;

    public CircularBuffer() {
        this(DEFAULT_CAPACITY);
    }

    @SuppressWarnings("unchecked")
    public CircularBuffer(final int capacity) {
        this.buffer = (T[]) new Object[capacity];
        this.capacity = buffer.length;
    }

    public T peek() {
        return buffer[head];
    }

    public void put(final T value) {
        if (isFull()) throw new UnsupportedOperationException("buffer full");
        buffer[tail++] = value;
        if (tail == capacity) tail = 0;
        size++;
    }

    public T take() {
        if (isEmpty()) throw new UnsupportedOperationException("buffer empty");
        final var value = buffer[head++];
        if (head == capacity) head = 0;
        size--;
        return value;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == capacity;
    }
}