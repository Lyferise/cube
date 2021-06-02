package com.lyferise.cube.node.wal;

import lombok.SneakyThrows;

import java.io.File;
import java.io.RandomAccessFile;

public class IndexFile {
    private final RandomAccessFile file;
    private long entryCount;

    @SneakyThrows
    public IndexFile(final String path) {

        // file
        final var f = new File(path);
        final var newFile = f.createNewFile();
        file = new RandomAccessFile(f, "rw");

        // header
        if (newFile) {
            file.writeLong(0);
        } else {
            entryCount = file.readLong();
        }
    }

    @SneakyThrows
    public long getPosition(final long sequence) {
        file.seek(getIndexPosition(sequence));
        return file.readLong();
    }

    @SneakyThrows
    public void append(final long sequence, final long position) {

        // index
        if (sequence != entryCount + 1) {
            throw new UnsupportedOperationException("WAL index check failed.");
        }
        file.seek(getIndexPosition(sequence));
        file.writeLong(position);

        // header
        file.seek(0);
        file.writeLong(++entryCount);
    }

    @SneakyThrows
    public void flush() {
        file.getChannel().force(true);
    }

    @SneakyThrows
    public void close() {
        file.close();
    }

    private static long getIndexPosition(final long sequence) {
        return 4 + sequence * 8;
    }
}