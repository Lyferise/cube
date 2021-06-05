package com.lyferise.cube.serialization;

import lombok.SneakyThrows;

import java.io.DataOutputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

public class DataOutputWriter implements BinaryWriter {
    protected DataOutputStream stream;

    protected DataOutputWriter() {
    }

    public DataOutputWriter(final DataOutputStream stream) {
        this.stream = stream;
    }

    @Override
    @SneakyThrows
    public void writeShort(final int value) {
        stream.writeShort(value);
    }

    @Override
    public void write(final String text) {
        write(text.getBytes(UTF_8));
    }

    @Override
    @SneakyThrows
    public void write(final byte[] data) {
        stream.writeInt(data.length);
        stream.write(data);
    }
}