package com.lyferise.cube.tables;

import java.util.ArrayList;
import java.util.List;

public class DataFrame {
    private final List<Row> rows = new ArrayList<>();

    public List<Row> getRows() {
        return rows;
    }
}