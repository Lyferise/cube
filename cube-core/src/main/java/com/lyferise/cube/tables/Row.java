package com.lyferise.cube.tables;

import com.lyferise.cube.serialization.BinaryReader;
import com.lyferise.cube.serialization.BinarySerializable;
import com.lyferise.cube.serialization.BinaryWriter;

import java.util.HashMap;
import java.util.Map;

public class Row implements BinarySerializable {
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

    @Override
    public void read(final BinaryReader reader) {
        final var n = reader.readInt();
        for (var i = 0; i < n; i++) {
            map.put(reader.readString(), reader.readObject());
        }
    }

    @Override
    public void write(final BinaryWriter writer) {
        writer.writeInt(map.size());
        for (final Map.Entry<String, Object> e : map.entrySet()) {
            writer.write(e.getKey());
            writer.writeObject(e.getValue());
        }
    }
}