package com.lyferise.cube.serialization;

import lombok.SneakyThrows;

import java.io.DataInput;

import static java.nio.charset.StandardCharsets.UTF_8;

public abstract class DataInputReader implements BinaryReader {
    protected final TypeMap typeMap;
    protected final DataInput in;

    @SneakyThrows
    public DataInputReader(final DataInput in) {
        this(TypeMap.EMPTY, in);
    }

    @SneakyThrows
    public DataInputReader(final TypeMap typeMap, final DataInput in) {
        this.typeMap = typeMap;
        this.in = in;
    }

    @Override
    @SneakyThrows
    public boolean readBoolean() {
        return in.readBoolean();
    }

    @Override
    @SneakyThrows
    public int readShort() {
        return in.readShort();
    }

    @Override
    @SneakyThrows
    public int readInt() {
        return in.readInt();
    }

    @Override
    @SneakyThrows
    public long readLong() {
        return in.readLong();
    }

    @Override
    @SneakyThrows
    public String readString() {
        return new String(readByteArray(), UTF_8);
    }

    public abstract byte[] readByteArray();

    @Override
    @SneakyThrows
    public BinarySerializable readSerializable() {
        final var name = readString();
        final var type = typeMap.get(name);
        if (type == null) throw new UnsupportedOperationException("Unrecognized type " + name);
        final var value = (BinarySerializable) type.getDeclaredConstructor().newInstance();
        value.read(this);
        return value;
    }
}