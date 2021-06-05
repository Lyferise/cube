package com.lyferise.cube.serialization;

import lombok.SneakyThrows;

import java.io.DataOutput;

import static java.nio.charset.StandardCharsets.UTF_8;

public class DataOutputWriter implements BinaryWriter {
    protected DataOutput out;

    protected DataOutputWriter() {
    }

    public DataOutputWriter(final DataOutput out) {
        this.out = out;
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
}