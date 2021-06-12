package com.lyferise.cube.lang.elements;

import com.lyferise.cube.lang.formatter.ElementFormatter;

import static com.lyferise.cube.lang.elements.ElementType.SYMBOL;

public class Symbol extends Element {
    private final SymbolType symbolType;

    public Symbol(final SymbolType symbolType) {
        super(SYMBOL);
        this.symbolType = symbolType;
    }

    public SymbolType getSymbolType() {
        return symbolType;
    }

    @Override
    public void format(final ElementFormatter formatter) {
        formatter.write(symbolType.getText());
    }

    @Override
    public boolean equals(final Object object) {
        if (!(object instanceof Symbol)) return false;
        return symbolType == ((Symbol) object).symbolType;
    }
}