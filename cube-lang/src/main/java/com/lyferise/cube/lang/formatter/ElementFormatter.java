package com.lyferise.cube.lang.formatter;

public class ElementFormatter {
    private final StringBuilder text = new StringBuilder();

    public void write(final String text) {
        this.text.append(text);
    }

    public void write(final char ch) {
        this.text.append(ch);
    }

    public void write(final int value) {
        this.text.append(value);
    }

    public void write(final long value) {
        this.text.append(value);
    }

    @Override
    public String toString() {
        return text.toString();
    }
}