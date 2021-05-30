package com.lyferise.cube.lang.elements;

import com.lyferise.cube.lang.formatter.ElementFormatter;

public abstract class Element {

    public abstract void format(final ElementFormatter formatter);

    @Override
    public String toString() {
        final ElementFormatter formatter = new ElementFormatter();
        format(formatter);
        return formatter.toString();
    }
}