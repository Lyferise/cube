package com.lyferise.cube.lang;

import java.util.Map;
import java.util.TreeMap;

public enum Keyword {
    NOT("not"),
    AND("and"),
    OR("or"),
    SELECT("select"),
    WHERE("where");

    private final String text;
    private static final Map<String, Keyword> map = buildMap();

    Keyword(final String text) {
        this.text = text;
    }

    public static Keyword of(final String name) {
        return map.get(name);
    }

    public String text() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }

    private static Map<String, Keyword> buildMap() {
        final var map = new TreeMap<String, Keyword>(String.CASE_INSENSITIVE_ORDER);
        for (final var keyword : values()) {
            map.put(keyword.text().toLowerCase(), keyword);
        }
        return map;
    }
}