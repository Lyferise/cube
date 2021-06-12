package com.lyferise.cube.lang.elements;

import com.lyferise.cube.lang.LanguageDefinition;
import com.lyferise.cube.lang.Operator;
import com.lyferise.cube.lang.formatter.ElementFormatter;

import static com.lyferise.cube.lang.elements.ElementType.BINARY_EXPRESSION;

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

        // left
        formatOperand(formatter, left);

        // operator
        formatter.write(' ');
        formatter.write(operator.getText());
        formatter.write(' ');

        // right
        formatOperand(formatter, right);
    }

    private void formatOperand(final ElementFormatter formatter, final Element element) {
        final var brackets = shouldBracket(formatter.getLanguageDefinition(), element);
        if (brackets) formatter.write('(');
        element.format(formatter);
        if (brackets) formatter.write(')');
    }

    private boolean shouldBracket(final LanguageDefinition languageDefinition, Element element) {
        return ((element instanceof BinaryExpression))
                && languageDefinition.bindingPower(((BinaryExpression) element).operator)
                < languageDefinition.bindingPower(operator);
    }

    @Override
    public boolean equals(final Object object) {
        if (!(object instanceof BinaryExpression)) return false;
        final var binaryExpression = (BinaryExpression) object;
        return left.equals(binaryExpression.left) && right.equals(binaryExpression.right);
    }
}