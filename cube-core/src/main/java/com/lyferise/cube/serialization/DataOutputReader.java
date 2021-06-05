package com.lyferise.cube.serialization;

import lombok.SneakyThrows;

import java.io.DataInputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

public class DataOutputReader implements BinaryReader {
    private final DataInputStream stream;

    @SneakyThrows
    public DataOutputReader(final DataInputStream stream) {
        this.stream = stream;
    }

    @Override
    @SneakyThrows
    public int readShort() {
        return stream.readShort();
    }

    @Override
    @SneakyThrows
    public String readString() {
        final var data = readByteArray();
        return new String(data, UTF_8);
    }

    @Override
    @SneakyThrows
    public byte[] readByteArray() {
        final var length = stream.readInt();
        final var data = new byte[length];
        var n = 0;
        while (n < length) n += stream.read(data, n, length - n);
        return data;
    }
}