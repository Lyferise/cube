package com.lyferise.cube.serialization;

import com.lyferise.cube.events.SpacetimeId;
import lombok.SneakyThrows;

import java.io.DataOutput;

import static com.lyferise.cube.serialization.TypeCode.*;
import static java.nio.charset.StandardCharsets.UTF_8;

public class DataOutputWriter implements BinaryWriter {
    private final TypeMap typeMap;
    protected DataOutput out;

    public DataOutputWriter(final DataOutput out) {
        this(TypeMap.EMPTY, out);
    }

    public DataOutputWriter(final TypeMap typeMap, final DataOutput out) {
        this.typeMap = typeMap;
        this.out = out;
    }

    @Override
    @SneakyThrows
    public void writeBoolean(final boolean value) {
        out.writeBoolean(value);
    }

    @Override
    @SneakyThrows
    public void writeShort(final int value) {
        out.writeShort(value);
    }

    @Override
    @SneakyThrows
    public void writeInt(final int value) {
        out.writeInt(value);
    }

    @Override
    @SneakyThrows
    public void writeLong(final long value) {
        out.writeLong(value);
    }

    @Override
    public void write(final String text) {
        write(text.getBytes(UTF_8));
    }

    @Override
    @SneakyThrows
    public void write(final byte[] data) {
        out.writeInt(data.length);
        out.write(data);
    }

    @Override
    public void write(final BinarySerializable value) {
        final var type = value.getClass();
        final var name = typeMap.nameOf(type);
        if (name == null) throw new UnsupportedOperationException("Unrecognized type " + type);
        write(name);
        value.write(this);
    }

    @Override
    @SneakyThrows
    public void writeObject(final Object value) {

        if (value == null) {
            out.writeByte(NONE.ordinal());
            return;
        }

        if (value instanceof BinarySerializable) {
            out.writeByte(OBJECT.ordinal());
            write((BinarySerializable) value);
            return;
        }

        if (value instanceof Integer) {
            out.writeByte(INT.ordinal());
            out.writeInt(((Integer) value));
            return;
        }

        if (value instanceof Long) {
            out.writeByte(LONG.ordinal());
            out.writeLong(((Long) value));
            return;
        }

        if (value instanceof Float) {
            out.writeByte(FLOAT.ordinal());
            out.writeFloat(((Float) value));
            return;
        }

        if (value instanceof Double) {
            out.writeByte(DOUBLE.ordinal());
            out.writeDouble(((Double) value));
            return;
        }

        if (value instanceof Boolean) {
            out.writeByte(BOOLEAN.ordinal());
            out.writeBoolean(((Boolean) value));
            return;
        }

        if (value instanceof String) {
            out.writeByte(STRING.ordinal());
            write(((String) value));
            return;
        }

        if (value instanceof Byte) {
            out.writeByte(BYTE.ordinal());
            out.writeByte(((Byte) value));
            return;
        }

        if (value instanceof Short) {
            out.writeByte(SHORT.ordinal());
            out.writeShort(((Short) value));
        }
    }

    @Override
    @SneakyThrows
    public void write(final SpacetimeId spacetimeId) {
        out.writeLong(spacetimeId.getSpace());
        out.writeLong(spacetimeId.getTime());
    }
}