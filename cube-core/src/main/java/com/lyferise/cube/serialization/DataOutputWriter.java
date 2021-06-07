package com.lyferise.cube.serialization;

import lombok.SneakyThrows;

import java.io.DataOutput;

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
}