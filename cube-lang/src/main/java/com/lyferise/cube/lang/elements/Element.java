package com.lyferise.cube.lang.elements;

import com.lyferise.cube.lang.formatter.ElementFormatter;

public abstract class Element {
    private final ElementType elementType;

    protected Element(final ElementType elementType) {
        this.elementType = elementType;
    }

    public ElementType getElementType() {
        return elementType;
    }

    public abstract void format(final ElementFormatter formatter);

    @Override
    public String toString() {
        final var formatter = new ElementFormatter();
        format(formatter);
        return formatter.toString();
    }
}