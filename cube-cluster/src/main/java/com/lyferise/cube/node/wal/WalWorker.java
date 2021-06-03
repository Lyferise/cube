package com.lyferise.cube.node.wal;

import com.lyferise.cube.node.configuration.WalConfiguration;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WalWorker extends Thread {
    private final Wal wal;
    private final WalEntry[] entries;
    private final int batchSize;

    public WalWorker(final WalConfiguration config, final Wal wal) {
        this.batchSize = config.getBatchSize();
        this.entries = new WalEntry[batchSize];
        this.wal = wal;
    }

    @Override
    @SneakyThrows
    public void run() {
        final var dataFile = wal.getDataFile();
        final var ringBuffer = wal.getRingBuffer();

        while (true) {
            wal.execute(() -> {
                try {
                    return writeAndOrDispatch();
                } catch (final Exception e) {
                    log.error("WAL worker exception", e);
                    return false;
                }
            });

            // if there is nothing to dispatch and the ring buffer is empty, wait 50 milliseconds
            if (!dataFile.canDispatch() && ringBuffer.isEmpty()) sleep(50);
        }
    }

    private boolean writeAndOrDispatch() {

        // wal
        final var dataFile = wal.getDataFile();
        final var indexFile = wal.getIndexFile();
        final var dispatcher = wal.getDispatcher();
        final var ringBuffer = wal.getRingBuffer();

        // drain
        final var entryCount = ringBuffer.moveTo(entries);
        var modified = false;

        // write
        var start = 0L;
        var end = 0L;
        if (entryCount > 0) {
            for (var i = 0; i < entryCount; i++) {
                final var entry = entries[i];
                indexFile.setPosition(entry.getSequence(), dataFile.write(entry));
            }
            start = entries[0].getSequence();
            end = entries[entryCount - 1].getSequence();
            modified = true;
        }

        // dispatch
        var dispatchCount = 0;
        while (dataFile.canDispatch() && dispatchCount < batchSize) {

            // entry
            final var sequence = dataFile.getDispatchSequence() + 1;
            final WalEntry entry;
            if (sequence >= start && sequence <= end) {
                entry = entries[(int) (sequence - start)];
            } else {
                entry = dataFile.read(indexFile.getPosition(sequence));
            }
            if (entry.getSequence() != sequence) {
                throw new UnsupportedOperationException("WAL dispatch sequence check failed");
            }

            // dispatch
            try {
                dispatcher.dispatch(entry);
            } catch (final Exception e) {
                e.printStackTrace();
                return modified;
            }
            dataFile.setDispatched(entry);
            modified = true;
            dispatchCount++;
        }

        // done
        return modified;
    }
}