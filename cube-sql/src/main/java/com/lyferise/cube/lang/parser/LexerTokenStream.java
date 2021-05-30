package com.lyferise.cube.lang.parser;

import com.lyferise.cube.lang.elements.Element;
import com.lyferise.cube.lang.elements.Symbol;

public class LexerTokenStream implements TokenStream {
    private final CubeLexer lexer;

    public LexerTokenStream(final CubeLexer lexer) {
        this.lexer = lexer;
    }

    @Override
    public Element next() {
        final ElementType elementType = lexer.next();
        if (elementType == null) return null;
        switch (elementType) {
            case SYMBOL:
                return new Symbol(lexer.getTokenText());
            default:
                throw new UnsupportedOperationException();
        }
    }
}