package com.lyferise.cube.lang.lexer;

import com.lyferise.cube.lang.Keyword;
import com.lyferise.cube.lang.elements.*;
import com.lyferise.cube.lang.elements.constants.IntConstant;

import static com.lyferise.cube.lang.elements.ElementType.*;
import static com.lyferise.cube.lang.elements.SymbolType.*;
import static java.lang.Integer.parseInt;

public class CubeLexer {
    private int position;
    private String text;
    private int length;
    private int tokenStart;
    private int tokenEnd;
    private ElementType tokenType;
    private SymbolType symbolType;

    public CubeLexer() {
    }

    public CubeLexer(final String text) {
        read(text);
    }

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
            case SYMBOL -> new Symbol(symbolType);
            case IDENTIFIER -> {
                final var text = getTokenText();
                final var keyword = Keyword.of(text);
                yield keyword != null ? new KeywordToken(keyword) : new Identifier(text);
            }
            case INT_CONSTANT -> new IntConstant(parseInt(getTokenText()));
            default -> throw new UnsupportedOperationException();
        };
    }

    public ElementType next() {

        // whitespace
        if (!canRead()) return null;
        while (canRead() && whitespace(peek())) position++;

        // number?
        if (!canRead()) return null;
        final var ch = peek();
        if (ch >= '0' && ch <= '9') return readNumber();
        if (ch == '-' || ch == '+') {
            final var ch2 = peek2();
            if (ch2 >= '0' && ch2 <= '9') return readNumber();
        }

        // identifier?
        return identifier(ch) ? readIdentifier() : readSymbol();
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

        switch (ch) {
            case '.' -> {
                n = 1;
                symbolType = DOT;
            }
            case '*' -> {
                n = 1;
                symbolType = ASTERISK;
            }
            case '\'' -> {
                n = 1;
                symbolType = SINGLE_QUOTE;
            }
            case '(' -> {
                n = 1;
                symbolType = OPEN_BRACKET;
            }
            case ')' -> {
                n = 1;
                symbolType = CLOSE_BRACKET;
            }
            case '-' -> {
                n = 1;
                symbolType = DASH;
            }
            case '+' -> {
                n = 1;
                symbolType = PLUS;
            }
            case '=' -> {
                n = 1;
                symbolType = EQUAL_SIGN;
            }
            case '>' -> {
                if (peek2() != '=') {
                    n = 1;
                    symbolType = GREATER_SIGN;
                } else {
                    n = 2;
                    symbolType = GREATER_OR_EQUAL_SIGN;
                }
            }
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

    private ElementType readNumber() {

        // +/-
        tokenStart = position;
        var ch = peek();
        if (ch == '+' || ch == '-') {
            position++;
        }

        // 0 ... 9
        while (canRead() && (ch = peek()) >= '0' && ch <= '9') {
            position++;
        }

        // token
        tokenEnd = position;
        return tokenType = INT_CONSTANT;
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