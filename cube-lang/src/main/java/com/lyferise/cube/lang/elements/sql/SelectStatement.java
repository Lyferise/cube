package com.lyferise.cube.lang.elements.sql;

import com.lyferise.cube.lang.elements.Element;
import com.lyferise.cube.lang.elements.ElementList;
import com.lyferise.cube.lang.formatter.ElementFormatter;

import static com.lyferise.cube.lang.elements.ElementList.singleElementList;
import static com.lyferise.cube.lang.elements.ElementType.SELECT_STATEMENT;

public class SelectStatement extends Element {
    private final ElementList selectList;
    private FromClause fromClause;
    private WhereClause whereClause;

    public SelectStatement(final ElementList selectList) {
        super(SELECT_STATEMENT);
        this.selectList = selectList;
    }

    public SelectStatement(final Element element, final FromClause fromClause) {
        super(SELECT_STATEMENT);
        this.selectList = singleElementList(element);
        this.fromClause = fromClause;
    }

    public SelectStatement(
            final Element element,
            final FromClause fromClause,
            final WhereClause whereClause) {
        super(SELECT_STATEMENT);
        this.selectList = singleElementList(element);
        this.fromClause = fromClause;
        this.whereClause = whereClause;
    }

    public ElementList getSelectList() {
        return selectList;
    }

    public FromClause getFromClause() {
        return fromClause;
    }

    public void setFromClause(final FromClause fromClause) {
        this.fromClause = fromClause;
    }

    public WhereClause getWhereClause() {
        return whereClause;
    }

    public void setWhereClause(WhereClause whereClause) {
        this.whereClause = whereClause;
    }

    @Override
    public void format(final ElementFormatter formatter) {

        // select
        formatter.write("select ");
        selectList.format(formatter);

        // from
        if (fromClause != null) {
            formatter.write(' ');
            fromClause.format(formatter);
        }

        // where
        if (whereClause != null) {
            formatter.write(' ');
            whereClause.format(formatter);
        }
    }
}