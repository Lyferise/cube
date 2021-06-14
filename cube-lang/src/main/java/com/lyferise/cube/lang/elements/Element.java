package com.lyferise.cube.lang.elements;

import com.lyferise.cube.lang.Keyword;
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

    public boolean is(final SymbolType symbolType) {
        return (this instanceof Symbol) && ((Symbol) this).getSymbolType() == symbolType;
    }

    public boolean is(final Keyword keyword) {
        return (this instanceof KeywordToken) && ((KeywordToken) this).getKeyword() == keyword;
    }
}