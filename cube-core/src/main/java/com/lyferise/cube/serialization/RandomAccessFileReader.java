package com.lyferise.cube.serialization;

import lombok.SneakyThrows;

import java.io.RandomAccessFile;

public class RandomAccessFileReader extends DataInputReader {

    public RandomAccessFileReader(final RandomAccessFile file) {
        super(file);
    }

    @Override
    @SneakyThrows
    public byte[] readByteArray() {
        final var file = (RandomAccessFile) in;
        final var length = file.readInt();
        final var data = new byte[length];
        var n = 0;
        while (n < length) n += file.read(data, n, length - n);
        return data;
    }
}