package com.lyferise.cube.lang.elements;

import com.lyferise.cube.lang.formatter.ElementFormatter;

import static com.lyferise.cube.lang.elements.ElementType.FUNCTION_EXPRESSION;

public class FunctionExpression extends Element {
    private final Element target;
    private ElementList parameters;

    public FunctionExpression(final Element target) {
        super(FUNCTION_EXPRESSION);
        this.target = target;
    }

    public FunctionExpression(final Element target, final ElementList parameters) {
        super(FUNCTION_EXPRESSION);
        this.target = target;
        this.parameters = parameters;
    }

    public Element getTarget() {
        return target;
    }

    public ElementList getParameters() {
        return parameters;
    }

    public void setParameters(ElementList parameters) {
        this.parameters = parameters;
    }

    @Override
    public void format(final ElementFormatter formatter) {
        target.format(formatter);
        formatter.write('(');
        if (parameters != null) parameters.format(formatter);
        formatter.write(')');
    }
}