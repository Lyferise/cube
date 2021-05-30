package com.lyferise.cube.lang.elements;

import com.lyferise.cube.lang.formatter.ElementFormatter;

public class Symbol extends Element {
    private final String text;

    public Symbol(final String text) {
        this.text = text;
    }

    @Override
    public void format(final ElementFormatter formatter) {
        formatter.write(text);
    }

    @Override
    public boolean equals(final Object object) {
        if (!(object instanceof Symbol)) return false;
        return text.equals(((Symbol) object).text);
    }
}