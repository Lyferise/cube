package com.lyferise.cube.serialization;

import lombok.SneakyThrows;

import java.io.DataInput;

import static java.nio.charset.StandardCharsets.UTF_8;

public abstract class DataInputReader implements BinaryReader {
    protected final DataInput in;

    @SneakyThrows
    public DataInputReader(final DataInput in) {
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
}