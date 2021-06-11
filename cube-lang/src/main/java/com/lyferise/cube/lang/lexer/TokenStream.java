package com.lyferise.cube.lang.lexer;

import com.lyferise.cube.lang.elements.Element;

import java.util.List;

public interface TokenStream {

    Element peek();

    Element next();

    static List<Element> tokenize(final String text) {
        return new LexerTokenStream(text).toList();
    }

    static TokenStream tokenStream(final String text) {
        return new BufferedTokenStream(new LexerTokenStream(text));
    }
}