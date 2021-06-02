package com.lyferise.cube.node.wal;

import com.lyferise.cube.concurrency.RingBuffer;
import com.lyferise.cube.node.configuration.WalConfiguration;
import lombok.SneakyThrows;

public class Wal {
    private final DataFile dataFile;
    private final IndexFile indexFile;
    private final RingBuffer<WalEntry> dispatchQueue;

    @SneakyThrows
    public Wal(final WalConfiguration config) {
        dataFile = new DataFile(config.getDataFile());
        indexFile = new IndexFile(config.getIndexFile());
        dispatchQueue = new RingBuffer<>(config.getDispatchQueueCapacity());

        // read
        for (var i = dataFile.getDispatchSequence(); i < dataFile.getWriteSequence(); i++) {
            var readPosition = indexFile.getPosition(i);
            if (!dispatchQueue.offer(dataFile.read(readPosition))) {
                throw new UnsupportedOperationException("WAL read queue full.");
            }
        }
    }

    @SneakyThrows
    public WalEntry read() {
        final WalEntry entry = dispatchQueue.take();
        dataFile.setDispatched(entry);
        return entry;
    }

    @SneakyThrows
    public void write(final WalEntry entry) {
        var position = dataFile.write(entry);
        indexFile.append(entry.getSequence(), position);
        dispatchQueue.put(entry);
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