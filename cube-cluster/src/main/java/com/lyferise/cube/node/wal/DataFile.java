package com.lyferise.cube.node.wal;

import com.lyferise.cube.events.SpacetimeId;
import com.lyferise.cube.internet.EndpointAddress;
import com.lyferise.cube.internet.IpAddress;
import lombok.SneakyThrows;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.UUID;

public class DataFile {
    private final RandomAccessFile file;
    private long writeSequence;
    private long writePosition;
    private long dispatchSequence;

    @SneakyThrows
    public DataFile(final String path) {

        // file
        final var f = new File(path);
        final var newFile = f.createNewFile();
        file = new RandomAccessFile(f, "rw");

        // header
        if (newFile) {
            file.writeLong(0);
            file.writeLong(0);
            flush();
        } else {
            writeSequence = file.readLong();
            dispatchSequence = file.readLong();
        }
        writePosition = file.length();
    }

    public boolean canDispatch() {
        return dispatchSequence < writeSequence;
    }

    public long getWriteSequence() {
        return writeSequence;
    }

    public long getDispatchSequence() {
        return dispatchSequence;
    }

    @SneakyThrows
    public void setDispatched(final WalEntry entry) {
        if (entry.getSequence() > dispatchSequence) {
            file.seek(8);
            file.writeLong(entry.getSequence());
            dispatchSequence = entry.getSequence();
        }
    }

    @SneakyThrows
    public long write(final WalEntry entry) {

        // check position
        final var position = writePosition;
        file.seek(position);
        file.writeLong(writePosition);

        // spacetime id
        final var spacetimeId = entry.getSpacetimeId();
        file.writeLong(spacetimeId.getSpace());
        file.writeLong(spacetimeId.getTime());

        // address
        final var address = entry.getAddress();
        file.write(address.getIpAddress().toByteArray(), 0, 16);
        file.writeShort(address.getPort());

        // session key
        final var sessionKey = entry.getSessionKey();
        file.writeLong(sessionKey.getMostSignificantBits());
        file.writeLong(sessionKey.getLeastSignificantBits());

        // data
        final var data = entry.getData();
        file.writeInt(data.length);
        file.write(data);
        file.writeInt(entry.getCrc());

        // done
        writePosition = file.getFilePointer();
        writeSequence = spacetimeId.getSequence();
        file.seek(0);
        file.writeLong(writeSequence);
        return position;
    }

    @SneakyThrows
    public void flush() {
        file.getChannel().force(true);
    }

    @SneakyThrows
    public void close() {
        file.close();
    }

    @SneakyThrows
    public WalEntry read(final long position) {

        // check position
        file.seek(position);
        final var checkPosition = file.readLong();
        if (position != checkPosition) {
            throw new UnsupportedOperationException("WAL position check failed");
        }

        // spacetime id
        final var spacetimeId = new SpacetimeId(file.readLong(), file.readLong());

        // address
        final var address = new EndpointAddress(new IpAddress(read(16)), file.readShort());

        // session key
        final var sessionKey = new UUID(file.readLong(), file.readLong());

        // data
        final var data = read(file.readInt());

        // crc
        final var crc = file.readInt();
        final var entry = new WalEntry(spacetimeId, address, sessionKey, data);
        if (entry.getCrc() != crc) {
            throw new UnsupportedOperationException("WAL CRC check failed");
        }
        return entry;
    }

    @SneakyThrows
    private byte[] read(final int length) {
        final var data = new byte[length];
        var n = 0;
        while (n < length) {
            n += file.read(data, n, length - n);
        }
        return data;
    }
}