package com.lyferise.cube.lang;

public enum Operator {
    ADD("+"),
    SUBTRACT("-"),
    MULTIPLY("*"),
    DIVIDE("/"),
    MODULO("%"),
    POSITIVE("+"),
    NEGATIVE("-"),
    NOT("not"),
    EQUAL("="),
    NOT_EQUAL("!="),
    GREATER(">"),
    LESS("<"),
    GREATER_OR_EQUAL(">="),
    LESS_OR_EQUAL("<=");

    private final String text;

    Operator(final String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}