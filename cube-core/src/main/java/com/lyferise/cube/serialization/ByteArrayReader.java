package com.lyferise.cube.serialization;

import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public class ByteArrayReader extends DataOutputReader {

    @SneakyThrows
    public ByteArrayReader(final byte[] data) {
        super(new DataInputStream(new ByteArrayInputStream(data)));
    }
}