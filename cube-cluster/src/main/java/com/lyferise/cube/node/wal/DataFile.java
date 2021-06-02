package com.lyferise.cube.node.wal;

import lombok.SneakyThrows;

import java.io.File;
import java.io.RandomAccessFile;

public class DataFile {
    private final RandomAccessFile file;
    private long readPosition;
    private long writePosition;
    private long entrySequence;

    @SneakyThrows
    public DataFile(final String path) {

        // file
        final var f = new File(path);
        final var newFile = f.createNewFile();
        file = new RandomAccessFile(f, "rw");

        // header
        if (newFile) {
            writePosition = 24;
            readPosition = 24;
            file.writeLong(0);
            file.writeLong(writePosition);
            file.writeLong(readPosition);
        } else {
            entrySequence = file.readLong();
            writePosition = file.readLong();
            readPosition = file.readLong();
        }
    }

    @SneakyThrows
    public WalEntry read() {

        // done?
        if (!canRead()) return null;

        // entry
        file.seek(readPosition);
        final var entrySequence = file.readLong();
        final var checkPosition = file.readLong();
        if (readPosition != checkPosition) {
            throw new UnsupportedOperationException("WAL position check failed");
        }
        final var length = file.readInt();
        final var crc = file.readInt();

        // data
        final var data = new byte[length];
        var n = 0;
        while (n < length) {
            n += file.read(data, n, length - n);
        }

        // CRC
        final WalEntry entry = new WalEntry(entrySequence, data);
        if (entry.getCrc() != crc) {
            throw new UnsupportedOperationException("WAL CRC check failed");
        }

        // header
        readPosition = file.getFilePointer();
        file.seek(16);
        file.writeLong(readPosition);
        return entry;
    }

    @SneakyThrows
    public long write(final WalEntry entry) {

        // entry
        entrySequence = entry.getSequence();
        final var data = entry.getData();

        // write
        final var position = writePosition;
        file.seek(position);
        file.writeLong(entrySequence);
        file.writeLong(writePosition);
        file.skipBytes(4);
        file.writeInt(data.length);
        file.writeInt(entry.getCrc());
        file.write(data);
        writePosition = file.getFilePointer();

        // header
        file.seek(0);
        file.writeLong(entrySequence);
        file.writeLong(writePosition);
        return writePosition;
    }

    @SneakyThrows
    public void flush() {
        file.getChannel().force(true);
    }

    @SneakyThrows
    public void close() {
        file.close();
    }

    // TODO: scope
    public boolean canRead() {
        return readPosition < writePosition;
    }
}