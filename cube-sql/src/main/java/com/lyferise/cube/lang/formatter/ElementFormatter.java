package com.lyferise.cube.lang.formatter;

public class ElementFormatter {
    private final StringBuilder text = new StringBuilder();

    public void write(final String text) {
        this.text.append(text);
    }

    @Override
    public String toString() {
        return text.toString();
    }
}