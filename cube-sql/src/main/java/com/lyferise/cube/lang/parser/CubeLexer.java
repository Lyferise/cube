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

    public ElementType next() {

        // done?
        if (position >= length) return null;

        // read
        return readSymbol();
    }

    private ElementType readSymbol() {

        // symbol
        int n = 0;
        final char ch = peek();
        if (ch == '.' || ch == '*' || ch == '+' || ch == '\'' || ch == '(' || ch == ')') {
            n = 1;
        } else if (ch == '=') {
            n = peek2() == '=' ? 2 : 1;
        }

        // not found?
        if (n == 0) {
            throw new UnsupportedOperationException("Unrecognized character: " + ch);
        }

        // token
        tokenStart = position;
        position += n;
        tokenEnd = position;
        return tokenType = SYMBOL;
    }

    private char peek() {
        return text.charAt(position);
    }

    private char peek2() {
        return text.charAt(position + 1);
    }
}