package com.lyferise.cube.lang.elements.sql;

import com.lyferise.cube.lang.elements.Element;
import com.lyferise.cube.lang.formatter.ElementFormatter;

import static com.lyferise.cube.lang.elements.ElementType.SUBSCRIBE_STATEMENT;

public class SubscribeStatement extends Element {
    private final Element source;
    private WhereClause whereClause;

    public SubscribeStatement(final Element source) {
        super(SUBSCRIBE_STATEMENT);
        this.source = source;
    }

    public SubscribeStatement(final Element source, final WhereClause whereClause) {
        super(SUBSCRIBE_STATEMENT);
        this.source = source;
        this.whereClause = whereClause;
    }

    public Element getSource() {
        return source;
    }

    public WhereClause getWhereClause() {
        return whereClause;
    }

    public void setWhereClause(WhereClause whereClause) {
        this.whereClause = whereClause;
    }

    @Override
    public void format(final ElementFormatter formatter) {

        // subscribe
        formatter.write("subscribe to ");
        source.format(formatter);

        // where
        if (whereClause != null) {
            formatter.write(' ');
            whereClause.format(formatter);
        }
    }
}