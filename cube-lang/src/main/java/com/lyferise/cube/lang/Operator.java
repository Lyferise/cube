package com.lyferise.cube.lang;

public enum Operator {
    ADD("+"),
    SUBTRACT("-"),
    MULTIPLY("*"),
    DIVIDE("/"),
    MODULO("%"),
    POSITIVE("+"),
    NEGATIVE("-"),
    NOT("not");

    private final String text;

    Operator(final String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}