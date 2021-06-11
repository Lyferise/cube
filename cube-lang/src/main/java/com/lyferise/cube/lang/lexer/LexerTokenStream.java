package com.lyferise.cube.lang.lexer;

import com.lyferise.cube.lang.elements.Element;

import java.util.ArrayList;
import java.util.List;

public class LexerTokenStream implements TokenStream {
    private final CubeLexer lexer;

    public LexerTokenStream(final String text) {
        this(new CubeLexer(text));
    }

    public LexerTokenStream(final CubeLexer lexer) {
        this.lexer = lexer;
    }

    @Override
    public Element peek() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Element next() {
        return lexer.next() == null ? null : lexer.getToken();
    }

    public List<Element> toList() {
        final var tokens = new ArrayList<Element>();
        Element token;
        while ((token = next()) != null) {
            tokens.add(token);
        }
        return tokens;
    }
}