package com.lyferise.cube.node.wal;

import lombok.SneakyThrows;

import java.io.File;
import java.io.RandomAccessFile;

public class WalFile {
    private final RandomAccessFile file;
    private long writePosition;

    public WalFile(final String name) {
        this(new File(name));
    }

    @SneakyThrows
    public WalFile(final File file) {
        file.createNewFile();
        this.file = new RandomAccessFile(file, "rw");
    }

    @SneakyThrows
    public void append(final WalEntry entry) {

        // entry
        final var sequence = entry.getSequence();
        final var data = entry.getData();

        // write
        file.writeLong(entry.getSequence());
        file.writeLong(writePosition);
        file.writeInt(data.length);
        file.writeInt(entry.getCrc());
        file.write(data);
        writePosition = file.getFilePointer();
        flush();
    }

    @SneakyThrows
    public void close() {
        file.close();
    }

    @SneakyThrows
    private void flush() {
        file.getChannel().force(true);
    }
}