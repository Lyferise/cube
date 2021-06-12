package com.lyferise.cube.lang;

public class UnsupportedOperatorException extends RuntimeException {

    public UnsupportedOperatorException(final Operator operator) {
        super("Unsupported operator " + operator);
    }
}