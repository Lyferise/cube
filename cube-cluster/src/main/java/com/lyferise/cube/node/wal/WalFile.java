package com.lyferise.cube.node.wal;

import com.lyferise.cube.concurrency.RingBuffer;
import com.lyferise.cube.node.configuration.WalConfiguration;
import lombok.SneakyThrows;

import java.io.File;
import java.io.RandomAccessFile;

public class WalFile {
    private final RandomAccessFile dataFile;
    private final RingBuffer<WalEntry> readQueue;
    private long readPosition;
    private long writePosition;
    private long entrySequence;

    @SneakyThrows
    public WalFile(final WalConfiguration config) {

        // file
        final var f = new File(config.getDataFile());
        final var newFile = f.createNewFile();
        dataFile = new RandomAccessFile(f, "rw");

        // header
        if (newFile) {
            writePosition = 24;
            readPosition = 24;
            dataFile.writeLong(0);
            dataFile.writeLong(writePosition);
            dataFile.writeLong(readPosition);
        } else {
            entrySequence = dataFile.readLong();
            writePosition = dataFile.readLong();
            readPosition = dataFile.readLong();
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
        dataFile.seek(writePosition);
        dataFile.writeLong(entrySequence);
        dataFile.writeLong(writePosition);
        dataFile.skipBytes(4);
        dataFile.writeInt(data.length);
        dataFile.writeInt(entry.getCrc());
        dataFile.write(data);
        writePosition = dataFile.getFilePointer();

        // header
        dataFile.seek(0);
        dataFile.writeLong(entrySequence);
        dataFile.writeLong(writePosition);

        // queue
        readQueue.put(entry);
    }

    @SneakyThrows
    public void flush() {
        dataFile.getChannel().force(true);
    }

    @SneakyThrows
    public void close() {
        dataFile.close();
    }

    @SneakyThrows
    private WalEntry next() {

        // done?
        if (!canRead()) return null;

        // entry
        dataFile.seek(readPosition);
        final var entrySequence = dataFile.readLong();
        final var checkPosition = dataFile.readLong();
        if (readPosition != checkPosition) {
            throw new UnsupportedOperationException("WAL position check failed");
        }
        final var length = dataFile.readInt();
        final var crc = dataFile.readInt();

        // data
        final var data = new byte[length];
        var n = 0;
        while (n < length) {
            n += dataFile.read(data, n, length - n);
        }

        // CRC
        final WalEntry entry = new WalEntry(entrySequence, data);
        if (entry.getCrc() != crc) {
            throw new UnsupportedOperationException("WAL CRC check failed");
        }

        // header
        readPosition = dataFile.getFilePointer();
        dataFile.seek(16);
        dataFile.writeLong(readPosition);
        return entry;
    }

    private boolean canRead() {
        return readPosition < writePosition;
    }
}