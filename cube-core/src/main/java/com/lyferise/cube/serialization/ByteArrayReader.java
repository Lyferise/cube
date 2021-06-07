package com.lyferise.cube.serialization;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public class ByteArrayReader extends DataInputStreamReader {

    public ByteArrayReader(final byte[] data) {
        super(wrap(data));
    }

    public ByteArrayReader(final TypeMap typeMap, final byte[] data) {
        super(typeMap, wrap(data));
    }

    private static DataInputStream wrap(final byte[] data) {
        return new DataInputStream(new ByteArrayInputStream(data));
    }
}