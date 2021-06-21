package com.lyferise.cube.lang.formatter;

import com.lyferise.cube.lang.elements.BinaryExpression;
import com.lyferise.cube.lang.elements.Element;
import com.lyferise.cube.lang.elements.Identifier;
import com.lyferise.cube.lang.elements.UnaryExpression;

public class TreeFormatter {
    private final StringBuilder text = new StringBuilder();

    public static String tree(final Element element) {
        final var formatter = new TreeFormatter();
        formatter.format(element);
        return formatter.toString();
    }

    public void format(final Element element) {
        format(element, 0);
    }

    @Override
    public String toString() {
        return text.toString();
    }

    private void format(final Element element, final int level) {

        // element
        text.append(" ".repeat(level * 2));
        text.append('<');
        text.append(element.getClass().getSimpleName());
        text.append("> ");
        formatElement(element);
        text.append('\n');

        // children
        final int childCount = element.getChildCount();
        for (var i = 0; i < childCount; i++) {
            format(element.getChild(i), level + 1);
        }
    }

    private void formatElement(final Element element) {
        if (element instanceof UnaryExpression) {
            text.append(((UnaryExpression) element).getOperator());
        } else if (element instanceof BinaryExpression) {
            text.append(((BinaryExpression) element).getOperator());
        } else if (element instanceof Identifier) {
            text.append(((Identifier) element).getText());
        } else {
            text.append("?");
        }
    }
}