package com.lyferise.cube.serialization;

import lombok.SneakyThrows;

import java.io.DataInputStream;

public class DataInputStreamReader extends DataInputReader {

    public DataInputStreamReader(final DataInputStream stream) {
        super(stream);
    }

    public DataInputStreamReader(final TypeMap typeMap, final DataInputStream stream) {
        super(typeMap, stream);
    }

    @Override
    @SneakyThrows
    public byte[] readByteArray() {
        final var stream = (DataInputStream) in;
        final var length = stream.readInt();
        final var data = new byte[length];
        var n = 0;
        while (n < length) n += stream.read(data, n, length - n);
        return data;
    }
}