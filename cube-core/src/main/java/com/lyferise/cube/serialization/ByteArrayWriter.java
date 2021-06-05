package com.lyferise.cube.serialization;

import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class ByteArrayWriter extends DataOutputWriter {
    private final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

    public ByteArrayWriter() {
        out = new DataOutputStream(byteStream);
    }

    @SneakyThrows
    public byte[] toByteArray() {
        ((DataOutputStream) out).flush();
        return byteStream.toByteArray();
    }
}