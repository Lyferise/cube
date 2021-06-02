package com.lyferise.cube.node.wal;

import lombok.SneakyThrows;

import java.io.File;
import java.io.RandomAccessFile;

public class WalFile {
    private final RandomAccessFile file;
    private long writePosition;
    private long entrySequence;

    public WalFile(final String path) {
        this(new File(path));
    }

    @SneakyThrows
    public WalFile(final File file) {
        var newFile = file.createNewFile();
        this.file = new RandomAccessFile(file, "rw");
        if (newFile) {
            this.file.writeLong(0);
            this.file.writeLong(8);
            writePosition = 16;
        } else {
            this.entrySequence = this.file.readLong();
            this.writePosition = this.file.readLong();
        }
    }

    public long getWritePosition() {
        return writePosition;
    }

    public long getEntrySequence() {
        return entrySequence;
    }

    @SneakyThrows
    public void append(final WalEntry entry) {

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