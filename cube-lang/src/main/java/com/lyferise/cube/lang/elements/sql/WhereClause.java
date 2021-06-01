package com.lyferise.cube.lang.elements.sql;

import com.lyferise.cube.lang.elements.Element;
import com.lyferise.cube.lang.formatter.ElementFormatter;

import static com.lyferise.cube.lang.elements.ElementType.WHERE_CLAUSE;

public class WhereClause extends Element {
    private final Element source;

    public WhereClause(final Element source) {
        super(WHERE_CLAUSE);
        this.source = source;
    }

    public Element getSource() {
        return source;
    }

    @Override
    public void format(final ElementFormatter formatter) {
        formatter.write("where ");
        source.format(formatter);
    }
}