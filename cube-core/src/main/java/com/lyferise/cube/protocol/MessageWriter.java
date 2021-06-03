package com.lyferise.cube.protocol;

import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

public class MessageWriter {
    private final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    private final DataOutputStream stream = new DataOutputStream(byteStream);

    @SneakyThrows
    public MessageWriter(final int messageCode) {
        stream.writeShort(messageCode);
    }

    public void write(final String text) {
        write(text.getBytes(UTF_8));
    }

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