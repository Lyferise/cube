package com.lyferise.cube.lang.parser;

import com.lyferise.cube.lang.elements.Element;
import com.lyferise.cube.lang.elements.ElementType;
import com.lyferise.cube.lang.elements.Identifier;
import com.lyferise.cube.lang.elements.Symbol;

import java.util.List;

import static com.lyferise.cube.lang.elements.ElementType.IDENTIFIER;
import static com.lyferise.cube.lang.elements.ElementType.SYMBOL;

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
        return switch (tokenType) {
            case SYMBOL -> new Symbol(getTokenText());
            case IDENTIFIER -> new Identifier(getTokenText());
            default -> throw new UnsupportedOperationException();
        };
    }

    public static List<Element> tokenize(final String text) {
        var lexer = new CubeLexer();
        lexer.read(text);
        return new LexerTokenStream(lexer).toList();
    }

    public ElementType next() {

        // whitespace
        if (!canRead()) return null;
        while (canRead() && whitespace(peek())) position++;

        // identifier
        if (!canRead()) return null;
        if (identifier(peek())) return readIdentifier();

        // symbol
        return readSymbol();
    }

    private ElementType readIdentifier() {

        // identifier
        tokenStart = position;
        while (canRead() && identifier(peek())) {
            position++;
        }

        // token
        tokenEnd = position;
        return tokenType = IDENTIFIER;
    }

    private ElementType readSymbol() {

        // symbol
        var n = 0;
        final var ch = peek();
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

    private boolean canRead() {
        return position < length;
    }

    private char peek() {
        return text.charAt(position);
    }

    private char peek2() {
        return text.charAt(position + 1);
    }

    private static boolean whitespace(final char ch) {
        return ch == ' ' || ch == '\t';
    }

    private static boolean identifier(final char ch) {
        return ch == '_' || (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9');
    }
}