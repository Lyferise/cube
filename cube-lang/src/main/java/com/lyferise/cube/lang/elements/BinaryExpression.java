package com.lyferise.cube.lang.elements;

import com.lyferise.cube.lang.Operator;
import com.lyferise.cube.lang.formatter.ElementFormatter;

import static com.lyferise.cube.lang.Operator.NOT;
import static com.lyferise.cube.lang.elements.ElementType.BINARY_EXPRESSION;
import static com.lyferise.cube.lang.elements.ElementType.UNARY_EXPRESSION;

public class BinaryExpression extends Element {
    private final Operator operator;
    private final Element left;
    private final Element right;

    public BinaryExpression(final Operator operator, final Element left, final Element right) {
        super(BINARY_EXPRESSION);
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    public Operator getOperator() {
        return operator;
    }

    public Element getLeft() {
        return left;
    }

    public Element getRight() {
        return right;
    }

    @Override
    public void format(final ElementFormatter formatter) {
        left.format(formatter);
        formatter.write(' ');
        formatter.write(operator.getText());
        formatter.write(' ');
        right.format(formatter);
    }
}