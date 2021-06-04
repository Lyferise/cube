package com.lyferise.cube.tables;

import java.util.HashMap;
import java.util.Map;

public class Row {
    private final Map<String, Object> map;

    public Row() {
        this.map = new HashMap<>();
    }

    public Row(final Map<String, Object> map) {
        this.map = map;
    }

    public Object get(final String field) {
        return map.get(field);
    }

    public Map<String, Object> getMap() {
        return map;
    }
}