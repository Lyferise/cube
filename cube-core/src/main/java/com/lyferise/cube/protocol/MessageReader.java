package com.lyferise.cube.protocol;

import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

public class MessageReader {
    private final DataInputStream stream;
    private final int messageCode;

    @SneakyThrows
    public MessageReader(final byte[] data) {
        this.stream = new DataInputStream(new ByteArrayInputStream(data));
        this.messageCode = stream.readShort();
    }

    public int getMessageCode() {
        return messageCode;
    }

    @SneakyThrows
    public String readString() {
        final var data = readByteArray();
        return new String(data, UTF_8);
    }

    @SneakyThrows
    public byte[] readByteArray() {
        final var length = stream.readInt();
        final var data = new byte[length];
        var n = 0;
        while (n < length) {
            n += stream.read(data, n, length - n);
        }
        return data;
    }
}