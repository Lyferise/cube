package com.lyferise.cube.node.wal;

import com.lyferise.cube.events.SpacetimeId;
import lombok.Value;

import java.util.UUID;
import java.util.zip.CRC32;

@Value
public class WalEntry {
    SpacetimeId spacetimeId;
    UUID sessionKey;
    byte[] data;

    public long getSequence() {
        return spacetimeId.getSequence();
    }

    public int getCrc() {
        final var crc = new CRC32();
        final var sequence = getSequence();
        crc.update((int) (sequence >> 32));
        crc.update((int) sequence);
        crc.update(data, 0, data.length);
        return (int) crc.getValue();
    }
}