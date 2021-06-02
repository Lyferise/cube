package com.lyferise.cube.node.wal;

import lombok.SneakyThrows;

import java.io.File;
import java.io.RandomAccessFile;

public class WalFile {
    private final RandomAccessFile file;
    private long readPosition;
    private long writePosition;
    private long entrySequence;

    public WalFile(final String path) {
        this(new File(path));
    }

    @SneakyThrows
    public WalFile(final File file) {
        var newFile = file.createNewFile();
        this.file = new RandomAccessFile(file, "rw");
        this.readPosition = 16;
        if (newFile) {
            this.file.writeLong(0);
            this.file.writeLong(8);
            this.writePosition = 16;
        } else {
            this.entrySequence = this.file.readLong();
            this.writePosition = this.file.readLong();
        }
    }

    @SneakyThrows
    public final WalEntry next() {

        // done?
        if (readPosition >= writePosition) return null;

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
        readPosition = file.getFilePointer();
        return entry;
    }

    @SneakyThrows
    public void write(final WalEntry entry) {

        // entry
        entrySequence = entry.getSequence();
        final var data = entry.getData();

        // write
        file.seek(writePosition);
        file.writeLong(entrySequence);
        file.writeLong(writePosition);
        file.writeInt(data.length);
        file.writeInt(entry.getCrc());
        file.write(data);
        writePosition = file.getFilePointer();

        // header
        file.seek(0);
        file.writeLong(entrySequence);
        file.writeLong(writePosition);
    }

    @SneakyThrows
    public void flush() {
        file.getChannel().force(true);
    }
}