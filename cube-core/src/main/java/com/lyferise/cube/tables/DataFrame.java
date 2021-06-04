package com.lyferise.cube.tables;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class DataFrame {
    private final List<Column> columns;
    private final List<Row> rows = new ArrayList<>();

    public DataFrame(final List<Column> columns) {
        this.columns = columns;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public List<Row> getRows() {
        return rows;
    }

    public int getRowCount() {
        return rows.size();
    }

    public boolean contains(final Predicate<Row> predicate) {
        return rows.stream().anyMatch(predicate);
    }
}