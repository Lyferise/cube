package com.lyferise.cube.node.wal;

import com.lyferise.cube.events.SpacetimeId;
import com.lyferise.cube.serialization.BinaryReader;
import com.lyferise.cube.serialization.BinarySerializable;
import com.lyferise.cube.serialization.BinaryWriter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.util.zip.CRC32;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalEntry implements BinarySerializable {
    SpacetimeId spacetimeId;
    UUID sessionKey;
    byte[] data;

    public long getSequence() {
        return spacetimeId.getSequence();
    }

    @Override
    public void read(final BinaryReader reader) {

        // entry
        spacetimeId = new SpacetimeId(reader.readLong(), reader.readLong());
        sessionKey = new UUID(reader.readLong(), reader.readLong());
        data = reader.readByteArray();

        // crc
        final var crc = reader.readInt();
        if (getCrc() != crc) {
            throw new UnsupportedOperationException("WAL CRC check failed");
        }
    }

    @Override
    public void write(final BinaryWriter writer) {

        // spacetime id
        writer.write(spacetimeId);

        // session key
        writer.writeLong(sessionKey.getMostSignificantBits());
        writer.writeLong(sessionKey.getLeastSignificantBits());

        // data
        writer.write(data);
        writer.writeInt(getCrc());
    }

    private int getCrc() {
        final var crc = new CRC32();
        final var sequence = getSequence();
        crc.update((int) (sequence >> 32));
        crc.update((int) sequence);
        crc.update(data, 0, data.length);
        return (int) crc.getValue();
    }
}