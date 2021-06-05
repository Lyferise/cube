package com.lyferise.cube.serialization;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public class ByteArrayReader extends DataInputStreamReader {

    public ByteArrayReader(final byte[] data) {
        super(new DataInputStream(new ByteArrayInputStream(data)));
    }
}