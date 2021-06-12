package com.lyferise.cube.lang.elements;

public enum SymbolType {
    DOT("."),
    ASTERISK("*"),
    SINGLE_QUOTE("'"),
    OPEN_BRACKET("("),
    CLOSE_BRACKET(")"),
    DASH("-"),
    PLUS("+"),
    EQUAL_SIGN("="),
    GREATER_SIGN(">"),
    GREATER_OR_EQUAL_SIGN(">=");

    private final String text;

    SymbolType(final String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}