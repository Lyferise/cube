package com.lyferise.cube.lang.parser;

import com.lyferise.cube.lang.elements.Element;
import com.lyferise.cube.lang.elements.Symbol;

import java.util.List;

import static com.lyferise.cube.lang.parser.ElementType.SYMBOL;

public class CubeLexer {
    private int position;
    private String text;
    private int length;
    private int tokenStart;
    private int tokenEnd;
    private ElementType tokenType;

    public void read(final String text) {
        this.text = text;
        this.length = text != null ? text.length() : 0;
        this.position = 0;
    }

    public ElementType next() {
        if (position >= length) return null;
        tokenStart = position++;
        tokenEnd = position;
        return tokenType = SYMBOL;
    }

    public String getTokenText() {
        return text.substring(tokenStart, tokenEnd);
    }

    public Element getToken() {
        if (tokenType == SYMBOL) return new Symbol(getTokenText());
        throw new UnsupportedOperationException();
    }

    public static List<Element> tokenize(final String text) {
        var lexer = new CubeLexer();
        lexer.read(text);
        return new LexerTokenStream(lexer).toList();
    }
}