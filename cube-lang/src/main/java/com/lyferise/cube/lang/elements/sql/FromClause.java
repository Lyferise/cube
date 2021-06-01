package com.lyferise.cube.lang.elements.sql;

import com.lyferise.cube.lang.elements.Element;
import com.lyferise.cube.lang.formatter.ElementFormatter;

import static com.lyferise.cube.lang.elements.ElementType.FROM_CLAUSE;

public class FromClause extends Element {
    private final Element source;

    public FromClause(final Element source) {
        super(FROM_CLAUSE);
        this.source = source;
    }

    public Element getSource() {
        return source;
    }

    @Override
    public void format(final ElementFormatter formatter) {
        formatter.write("from ");
        source.format(formatter);
    }
}