package com.lyferise.cube.lang.parser;

import com.lyferise.cube.lang.elements.Element;
import com.lyferise.cube.lang.elements.Symbol;

import java.util.ArrayList;
import java.util.List;

import static com.lyferise.cube.lang.parser.ElementType.SYMBOL;

public class CubeLexer {
    private int position;
    private String text;
    private int length;
    private int tokenStart;
    private int tokenEnd;

    public void read(final String text) {
        this.text = text;
        this.length = text != null ? text.length() : 0;
        this.position = 0;
    }

    public ElementType next() {
        if (position >= length) return null;
        tokenStart = position++;
        tokenEnd = position;
        return SYMBOL;
    }

    public String getTokenText() {
        return text.substring(tokenStart, tokenEnd);
    }

    public Element getToken() {
        return new Symbol(getTokenText());
    }

    public static List<Element> tokenize(final String text) {
        final var tokens = new ArrayList<Element>();
        final var lexer = new CubeLexer();
        lexer.read(text);
        while (lexer.next() != null) {
            tokens.add(lexer.getToken());
        }
        return tokens;
    }
}