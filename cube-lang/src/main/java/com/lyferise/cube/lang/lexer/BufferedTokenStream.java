package com.lyferise.cube.lang.lexer;

import com.lyferise.cube.collections.CircularBuffer;
import com.lyferise.cube.lang.elements.Element;

public class BufferedTokenStream implements TokenStream {
    private final CircularBuffer<Element> circularBuffer = new CircularBuffer<>();
    private final TokenStream tokens;
    private boolean endOfStream;

    public BufferedTokenStream(final TokenStream tokens) {
        this.tokens = tokens;
    }

    @Override
    public Element peek() {
        return !ensure(1) ? null : circularBuffer.peek();
    }

    @Override
    public Element next() {
        return !ensure(1) ? null : circularBuffer.take();
    }

    private boolean ensure(final int count) {
        if (circularBuffer.size() < count) fill();
        return !circularBuffer.isEmpty();
    }

    private void fill() {
        if (endOfStream) return;
        while (!circularBuffer.isFull()) {
            final var token = tokens.next();
            if (token == null) {
                endOfStream = true;
                return;
            }
            circularBuffer.put(token);
        }
    }
}