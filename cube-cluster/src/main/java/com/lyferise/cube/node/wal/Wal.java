package com.lyferise.cube.node.wal;

import com.lyferise.cube.concurrency.RingBuffer;
import com.lyferise.cube.node.configuration.WalConfiguration;
import lombok.SneakyThrows;

public class Wal {
    private final DataFile dataFile;
    private final IndexFile indexFile;
    private final RingBuffer<WalEntry> readQueue;

    @SneakyThrows
    public Wal(final WalConfiguration config) {
        dataFile = new DataFile(config.getDataFile());
        indexFile = new IndexFile(config.getIndexFile());
        readQueue = new RingBuffer<>(config.getReadQueueCapacity());
        while (dataFile.canRead()) {
            if (!readQueue.offer(dataFile.read())) {
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
        dataFile.write(entry);
        readQueue.put(entry);
    }

    @SneakyThrows
    public void flush() {
        dataFile.flush();
        indexFile.flush();
    }

    @SneakyThrows
    public void close() {
        dataFile.close();
        indexFile.close();
    }
}