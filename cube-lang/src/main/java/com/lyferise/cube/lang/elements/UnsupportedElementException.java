package com.lyferise.cube.lang.elements;

public class UnsupportedElementException extends RuntimeException {

    public UnsupportedElementException(final Element element) {
        super("Unsupported element " + element.getClass() + " " + element);
    }
}