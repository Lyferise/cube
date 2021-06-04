package com.lyferise.cube.tables;

import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

public class DataFrame {
    private final List<Column> columns;
    private final List<Row> rows = new ArrayList<>();

    public DataFrame(final String... columns) {
        this(stream(columns).map(Column::new).collect(toList()));
    }

    public DataFrame(final List<Column> columns) {
        this.columns = columns;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public int getColumnCount() {
        return columns.size();
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

    @Override
    public String toString() {

        // format
        final var rows = formatRows();
        final var widths = computeColumnWidths(rows);

        // header
        final var text = new StringBuilder();
        writeSeparator(text, widths);
        text.append('\n');
        writeRow(text, widths, columns.stream().map(Column::getName).toArray(String[]::new));

        // rows
        for (final var row : rows) {
            text.append('\n');
            writeSeparator(text, widths);
            text.append('\n');
            writeRow(text, widths, row.text);
        }

        // end
        text.append('\n');
        writeSeparator(text, widths);
        return text.toString();
    }

    private static void writeRow(final StringBuilder text, final int[] widths, final String[] values) {
        final var columnCount = widths.length;
        for (var i = 0; i < columnCount; i++) {
            final var value = values[i];
            text.append('|').append(' ').append(value).append(" ".repeat(widths[i] - value.length() + 1));
        }
        text.append('|');
    }

    private static void writeSeparator(final StringBuilder text, final int[] widths) {
        for (int width : widths) {
            text.append('+').append("-".repeat(width + 2));
        }
        text.append('+');
    }

    private int[] computeColumnWidths(final List<RowFormat> rows) {
        final var columnCount = columns.size();
        final var widths = new int[columnCount];

        // header
        for (var i = 0; i < columnCount; i++) {
            final var width = columns.get(i).getName().length();
            if (widths[i] < width) widths[i] = width;
        }

        // rows
        for (final var row : rows) {
            final var text = row.text;
            for (var i = 0; i < columnCount; i++) {
                final var width = text[i].length();
                if (widths[i] < width) widths[i] = width;
            }
        }
        return widths;
    }

    private List<RowFormat> formatRows() {
        final var rows = new ArrayList<RowFormat>();
        for (final var row : this.rows) {
            rows.add(formatRow(row));
        }
        return rows;
    }

    private RowFormat formatRow(final Row row) {
        final var columnCount = columns.size();
        final var text = new String[columnCount];
        var i = 0;
        for (var column : columns) {
            final var value = row.get(column.getName());
            text[i++] = value == null ? "null" : value.toString();
        }
        return new RowFormat(text);
    }

    @Value
    private static class RowFormat {
        String[] text;
    }
}