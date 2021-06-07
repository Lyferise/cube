package com.lyferise.cube.serialization;

import java.util.HashMap;
import java.util.Map;

public class TypeMap {
    public static final TypeMap EMPTY = new TypeMap();

    private final Map<String, Class<?>> nameMap = new HashMap<>();
    private final Map<Class<?>, String> typeMap = new HashMap<>();

    public void add(final String name, final Class<?> type) {
        nameMap.put(name, type);
        typeMap.put(type, name);
    }

    public Class<?> get(final String name) {
        return nameMap.get(name);
    }

    public String nameOf(final Class<?> type) {
        return typeMap.get(type);
    }

}