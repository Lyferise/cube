package com.lyferise.cube.tables;

import java.util.HashMap;
import java.util.Map;

public class Row {
    private final Map<String, Object> map = new HashMap<>();

    public Object get(final String field) {
        return map.get(field);
    }

    public void set(final String field, final Object value) {
        map.put(field, value);
    }
}