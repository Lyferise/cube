package com.lyferise.cube.node.wal;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.zip.CRC32;

@Value
@AllArgsConstructor
public class WalEntry {
    long sequence;
    byte[] data;
    int crc;

    public WalEntry(final long sequence, byte[] data) {
        this(sequence, data, getCrc(sequence, data));
    }

    private static int getCrc(final long sequence, final byte[] data) {
        final var crc = new CRC32();
        crc.update((int) (sequence >> 32));
        crc.update((int) sequence);
        crc.update(data, 0, data.length);
        return (int) crc.getValue();
    }
}