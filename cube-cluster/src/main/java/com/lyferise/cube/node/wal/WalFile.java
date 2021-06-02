package com.lyferise.cube.node.wal;

import com.lyferise.cube.concurrency.RingBuffer;
import com.lyferise.cube.node.configuration.WalConfiguration;
import lombok.SneakyThrows;

import java.io.File;
import java.io.RandomAccessFile;

public class WalFile {
    private final RandomAccessFile file;
    private final RingBuffer<WalEntry> readQueue;
    private long readPosition;
    private long writePosition;
    private long entrySequence;

    @SneakyThrows
    public WalFile(final WalConfiguration config) {

        // file
        final var f = new File(config.getPath());
        final var newFile = f.createNewFile();
        file = new RandomAccessFile(f, "rw");

        // header
        readPosition = 16;
        if (newFile) {
            file.writeLong(0);
            file.writeLong(8);
            writePosition = 16;
        } else {
            entrySequence = file.readLong();
            writePosition = file.readLong();
        }

        // queue
        readQueue = new RingBuffer<>(config.getReadQueueCapacity());
        while (canRead()) {
            if (!readQueue.offer(next())) {
                throw new UnsupportedOperationException("WAL read queue full.");
            }
        }
    }

    @SneakyThrows
    public WalEntry read() {
        return readQueue.take();
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

        // queue
        readQueue.put(entry);
    }

    @SneakyThrows
    public void flush() {
        file.getChannel().force(true);
    }

    @SneakyThrows
    private WalEntry next() {

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
        readPosition = file.getFilePointer();
        return entry;
    }

    private boolean canRead() {
        return readPosition < writePosition;
    }
}