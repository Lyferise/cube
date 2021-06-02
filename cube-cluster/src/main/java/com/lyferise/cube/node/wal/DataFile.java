package com.lyferise.cube.node.wal;

import lombok.SneakyThrows;

import java.io.File;
import java.io.RandomAccessFile;

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

        // entry
        writeSequence = entry.getSequence();
        final var data = entry.getData();

        // write
        final var position = writePosition;
        file.seek(position);
        file.writeLong(writeSequence);
        file.writeLong(writePosition);
        file.skipBytes(4);
        file.writeInt(data.length);
        file.writeInt(entry.getCrc());
        file.write(data);
        writePosition = file.getFilePointer();

        // header
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

        // entry
        file.seek(position);
        var sequence = file.readLong();
        final var checkPosition = file.readLong();
        if (position != checkPosition) {
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
        final WalEntry entry = new WalEntry(sequence, data);
        if (entry.getCrc() != crc) {
            throw new UnsupportedOperationException("WAL CRC check failed");
        }
        return entry;
    }
}