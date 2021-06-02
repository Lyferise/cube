package com.lyferise.cube.node.wal;

import com.lyferise.cube.concurrency.RingBuffer;
import com.lyferise.cube.node.configuration.WalConfiguration;
import lombok.SneakyThrows;

import java.util.concurrent.locks.ReentrantLock;

public class Wal {
    private final ReentrantLock reentrantLock = new ReentrantLock();
    private final DataFile dataFile;
    private final IndexFile indexFile;
    private final RingBuffer<WalEntry> ringBuffer;
    private final WalDispatcher dispatcher;
    private final WalWorker walWorker;

    @SneakyThrows
    public Wal(final WalConfiguration config, final WalDispatcher dispatcher) {
        this.dataFile = new DataFile(config.getDataFile());
        this.indexFile = new IndexFile(config.getIndexFile());
        this.ringBuffer = new RingBuffer<>(config.getRingBufferCapacity());
        this.dispatcher = dispatcher;
        this.walWorker = new WalWorker(config, this);
        this.walWorker.start();
    }

    public DataFile getDataFile() {
        return dataFile;
    }

    public IndexFile getIndexFile() {
        return indexFile;
    }

    public RingBuffer<WalEntry> getRingBuffer() {
        return ringBuffer;
    }

    public WalDispatcher getDispatcher() {
        return dispatcher;
    }

    public void execute(final Runnable action) {
        try {
            reentrantLock.lock();
            action.run();
            flush();
        } finally {
            reentrantLock.unlock();
        }
    }

    @SneakyThrows
    public void write(final WalEntry entry) {
        ringBuffer.put(entry);
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