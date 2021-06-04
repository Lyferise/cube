package com.lyferise.cube.serialization;

import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ByteArrayWriter implements BinaryWriter {
    private final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    private final DataOutputStream stream = new DataOutputStream(byteStream);

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

    @SneakyThrows
    public byte[] toByteArray() {
        stream.flush();
        return byteStream.toByteArray();
    }
}