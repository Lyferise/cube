package com.lyferise.cube.lang.parser;

import com.lyferise.cube.lang.Keyword;
import com.lyferise.cube.lang.LanguageDefinition;
import com.lyferise.cube.lang.elements.*;
import com.lyferise.cube.lang.elements.constants.Constant;
import com.lyferise.cube.lang.lexer.TokenStream;

import static com.lyferise.cube.lang.Operator.*;
import static com.lyferise.cube.lang.elements.SymbolType.*;
import static com.lyferise.cube.lang.formatter.ElementFormatter.format;
import static com.lyferise.cube.lang.lexer.TokenStream.tokenStream;

public class CubeParser {
    private final LanguageDefinition languageDefinition;
    private final TokenStream tokens;

    public CubeParser(final LanguageDefinition languageDefinition, TokenStream tokens) {
        this.languageDefinition = languageDefinition;
        this.tokens = tokens;
    }

    public static Element parse(final LanguageDefinition languageDefinition, final String text) {
        return new CubeParser(languageDefinition, tokenStream(text)).parseElement();
    }

    public Element parseElement() {
        return parseElement(0);
    }

    public Element parseElement(final int rightBindingPower) {
        var left = prefixHandler(tokens.next());
        Element right;
        int bindingPower;
        while ((right = tokens.peek()) != null
                && (bindingPower = languageDefinition.bindingPower(right)) > rightBindingPower) {
            left = infixHandler(left, tokens.next(), bindingPower);
        }
        return left;
    }

    private Element prefixHandler(final Element element) {
        if (element instanceof Constant) return element;
        if (element instanceof Identifier) return element;

        // -
        if (element.is(DASH)) {
            return new UnaryExpression(NEGATIVE, parseElement());
        }

        // not
        if (element.is(Keyword.NOT)) {
            return new UnaryExpression(NOT, parseElement());
        }

        // ( ... )
        if (element.is(OPEN_BRACKET)) {
            final var element2 = parseElement();
            match(CLOSE_BRACKET);
            return element2;
        }

        throw new UnsupportedElementException(languageDefinition, element);
    }

    private Element infixHandler(final Element left, final Element right, int rightBindingPower) {
        if (right.is(PLUS)) return new BinaryExpression(ADD, left, parseElement(rightBindingPower));
        if (right.is(ASTERISK)) return new BinaryExpression(MULTIPLY, left, parseElement(rightBindingPower));
        throw new UnsupportedElementException(languageDefinition, right);
    }

    private void match(final SymbolType symbolType) {
        final var token = tokens.next();
        if (token == null) {
            throw new UnsupportedOperationException("expected " + symbolType);
        }
        if (!token.is(symbolType)) {
            throw new UnsupportedOperationException(
                    "expected " + symbolType + " not " + format(languageDefinition, token));
        }
    }
}