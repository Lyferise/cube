package com.lyferise.cube.lang.elements;

import com.lyferise.cube.lang.formatter.ElementFormatter;

import static com.lyferise.cube.lang.elements.ElementType.IDENTIFIER;

public class Identifier extends Element {
    private final String text;

    public Identifier(final String text) {
        super(IDENTIFIER);
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public void format(final ElementFormatter formatter) {
        formatter.write(text);
    }

    @Override
    public boolean equals(final Object object) {
        if (!(object instanceof Identifier)) return false;
        return text.equals(((Identifier) object).text);
    }
}