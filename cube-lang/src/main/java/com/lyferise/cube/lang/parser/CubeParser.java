package com.lyferise.cube.lang.parser;

import com.lyferise.cube.lang.elements.BinaryExpression;
import com.lyferise.cube.lang.elements.Element;
import com.lyferise.cube.lang.elements.Symbol;
import com.lyferise.cube.lang.elements.UnsupportedElementException;
import com.lyferise.cube.lang.elements.constants.Constant;
import com.lyferise.cube.lang.lexer.TokenStream;

import static com.lyferise.cube.lang.Operator.ADD;
import static com.lyferise.cube.lang.lexer.TokenStream.tokenStream;

public class CubeParser {
    private final TokenStream tokens;

    public CubeParser(final TokenStream tokens) {
        this.tokens = tokens;
    }

    public static Element parse(final String text) {
        return new CubeParser(tokenStream(text)).parseElement();
    }

    public Element parseElement() {
        return parseElement(0);
    }

    public Element parseElement(final int rightBindingPower) {
        var left = prefixHandler(tokens.next());
        Element right;
        while ((right = tokens.peek()) != null && rightBindingPower < bindingPower(right)) {
            left = infixHandler(left, tokens.next());
        }
        return left;
    }

    private Element prefixHandler(final Element element) {
        if (element instanceof Constant) return element;
        throw new UnsupportedElementException(element);
    }

    private Element infixHandler(final Element left, final Element right) {
        if (right instanceof Symbol) {
            if (((Symbol) right).getText().equals("+")) {
                return new BinaryExpression(ADD, left, parseElement());
            }
        }
        throw new UnsupportedElementException(right);
    }

    private int bindingPower(final Element element) {
        if (element instanceof Symbol) {
            if (((Symbol) element).getText().equals("+")) return 1;
        }
        throw new UnsupportedElementException(element);
    }
}