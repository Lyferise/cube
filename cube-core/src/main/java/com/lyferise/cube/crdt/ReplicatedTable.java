package com.lyferise.cube.crdt;

import com.lyferise.cube.events.SpacetimeId;
import com.lyferise.cube.tables.Column;
import com.lyferise.cube.tables.DataFrame;
import com.lyferise.cube.tables.Row;
import lombok.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public class ReplicatedTable extends AbstractDeltaCrdt {
    private final Map<SpacetimeId, ReplicatedRow> rows = new HashMap<>();
    private final String primaryKeyField;

    public ReplicatedTable(final String primaryKeyField) {
        this.primaryKeyField = primaryKeyField;
    }

    public DataFrame select(final String... columns) {
        return ((DataFrameResult) read(new Select(asList(columns)))).getDataFrame();
    }

    @Override
    public QueryResult read(final Query query) {
        if (!(query instanceof Select)) throw new UnsupportedOperationException();

        // columns
        final var columns = ((Select) query).columns.stream().map(Column::new).collect(toList());
        final var dataFrame = new DataFrame(columns);

        // rows
        final var rows = dataFrame.getRows();
        for (final var e1 : this.rows.entrySet()) {

            // primary key
            final var row = new Row();
            final var map = row.getMap();
            map.put(primaryKeyField, e1.getKey());

            // fields
            for (final var e2 : e1.getValue().map.entrySet()) {
                final var value = e2.getValue().value;
                if (value != null) map.put(e2.getKey(), value);
            }
            rows.add(row);
        }

        return new DataFrameResult(dataFrame);
    }

    @Override
    public Delta mutate(final Mutator mutator) {
        if (!(mutator instanceof UpsertRow)) throw new UnsupportedOperationException();
        final var upsertRow = (UpsertRow) mutator;
        upsert(upsertRow);
        return upsertRow;
    }

    @Override
    public void merge(final Delta delta) {
        if (!(delta instanceof UpsertRow)) throw new UnsupportedOperationException();
        upsert((UpsertRow) delta);
    }

    @Value
    public static class Select implements Query {
        List<String> columns;
    }

    @Value
    public static class DataFrameResult implements QueryResult {
        DataFrame dataFrame;
    }

    @Value
    public static class UpsertRow implements Mutator, Delta {
        SpacetimeId updated;
        SpacetimeId primaryKey;
        Row row;
    }

    private void upsert(final UpsertRow upsertRow) {

        // row
        final var primaryKey = upsertRow.primaryKey;
        var replicatedRow = rows.get(primaryKey);
        if (replicatedRow == null) {
            replicatedRow = new ReplicatedRow();
            rows.put(primaryKey, replicatedRow);
        }

        // map
        var map = replicatedRow.map;
        if (map == null) {
            map = new HashMap<>();
            replicatedRow.map = map;
        }

        // fields
        final var updated = upsertRow.updated;
        for (final var e : upsertRow.row.getMap().entrySet()) {
            final var field = e.getKey();

            // cell
            var cell = map.get(field);
            if (cell == null) {
                cell = new Cell();
                map.put(field, cell);
            }

            // upsert
            if (cell.updated == null || cell.updated.compareTo(updated) < 0) {
                cell.value = e.getValue();
            }
        }
    }

    private static class ReplicatedRow {
        Map<String, Cell> map;
    }

    private static class Cell {
        SpacetimeId updated;
        Object value;
    }
}