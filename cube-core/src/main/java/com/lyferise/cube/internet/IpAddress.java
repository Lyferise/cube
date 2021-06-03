package com.lyferise.cube.internet;

import lombok.SneakyThrows;

import java.net.InetAddress;

import static java.lang.Integer.toHexString;

public class IpAddress {
    private final byte[] data;

    public IpAddress(final byte[] data) {
        this.data = data.length == 16 ? data : mapIPv4Address(data);
    }

    public IpAddress(final InetAddress address) {
        this(address.getAddress());
    }

    @SneakyThrows
    public static IpAddress getLocalhost() {
        return new IpAddress(InetAddress.getLocalHost());
    }

    public byte[] toByteArray() {
        return data;
    }

    @Override
    public String toString() {
        final var text = new StringBuilder();
        if (isMapIPv4Address()) {
            for (var i = 0; i < 4; i++) {
                if (i > 0) text.append('.');
                text.append(data[i + 12] & 0xFF);
            }
        } else {
            for (int i = 0; i < 8; i++) {
                if (i > 0) text.append(':');
                text.append(toHexString(((data[i << 1] << 8) & 0xFF00) | (data[(i << 1) + 1] & 0xFF)));
            }
        }
        return text.toString();
    }

    public boolean isMapIPv4Address() {
        return isIPv4MappedAddress(data);
    }

    private static byte[] mapIPv4Address(final byte[] data) {
        final var buffer = new byte[16];
        buffer[10] = (byte) 0xFF;
        buffer[11] = (byte) 0xFF;
        buffer[12] = data[0];
        buffer[13] = data[1];
        buffer[14] = data[2];
        buffer[15] = data[3];
        return buffer;
    }

    public static boolean isIPv4MappedAddress(final byte[] data) {
        return data.length == 16
                && data[0] == 0 && data[1] == 0 && data[2] == 0 && data[3] == 0
                && data[4] == 0 && data[5] == 0 && data[6] == 0 && data[7] == 0
                && data[8] == 0 && data[9] == 0 && data[10] == (byte) 0xFF && data[11] == (byte) 0xFF;
    }
}