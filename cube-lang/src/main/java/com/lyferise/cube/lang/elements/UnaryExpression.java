package com.lyferise.cube.lang.elements;

import com.lyferise.cube.lang.Operator;
import com.lyferise.cube.lang.formatter.ElementFormatter;

import static com.lyferise.cube.lang.Operator.NOT;
import static com.lyferise.cube.lang.elements.ElementType.UNARY_EXPRESSION;

public class UnaryExpression extends Element {
    private final Operator operator;
    private final Element element;

    public UnaryExpression(final Operator operator, final Element element) {
        super(UNARY_EXPRESSION);
        this.operator = operator;
        this.element = element;
    }

    public Operator getOperator() {
        return operator;
    }

    public Element getElement() {
        return element;
    }

    @Override
    public int getChildCount() {
        return 1;
    }

    @Override
    public Element getChild(final int index) {
        if (index != 0) throw new IndexOutOfBoundsException(index);
        return element;
    }

    @Override
    public void format(final ElementFormatter formatter) {

        // operator
        formatter.write(operator.getText());
        if (operator == NOT) formatter.write(' ');

        // operand
        final var brackets = shouldBracket();
        if (brackets) formatter.write('(');
        element.format(formatter);
        if (brackets) formatter.write(')');

    }

    @Override
    public boolean equals(final Object object) {
        if (!(object instanceof UnaryExpression)) return false;
        final var unaryExpression = (UnaryExpression) object;
        return this.operator.equals(unaryExpression.operator) && element.equals(unaryExpression.element);
    }

    private boolean shouldBracket() {
        return element instanceof BinaryExpression;
    }
}