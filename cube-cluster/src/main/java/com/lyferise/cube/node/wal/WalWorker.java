package com.lyferise.cube.node.wal;

import com.lyferise.cube.node.configuration.WalConfiguration;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;

public class WalWorker extends Thread {
    private final Wal wal;
    private final List<WalEntry> entries = new ArrayList<>();
    private final int batchSize;

    public WalWorker(final WalConfiguration config, final Wal wal) {
        this.batchSize = config.getBatchSize();
        this.wal = wal;
    }

    @Override
    @SneakyThrows
    public void run() {
        final var dataFile = wal.getDataFile();
        final var ringBuffer = wal.getRingBuffer();
        final var signal = wal.getSignal();

        while (true) {
            wal.execute(this::writeAndOrDispatch);

            // if there is nothing to dispatch and the ring buffer is empty, wait 50 milliseconds
            if (!dataFile.canDispatch() && ringBuffer.isEmpty()) signal.await(50);
        }
    }

    private boolean writeAndOrDispatch() {

        // wal
        final var dataFile = wal.getDataFile();
        final var indexFile = wal.getIndexFile();
        final var dispatcher = wal.getDispatcher();

        // drain
        entries.clear();
        wal.getRingBuffer().drainTo(entries, batchSize);
        var modified = false;

        // write
        var start = 0L;
        var end = 0L;
        if (!entries.isEmpty()) {
            for (final var entry : entries) {
                indexFile.setPosition(entry.getSequence(), dataFile.write(entry));
            }
            start = entries.get(0).getSequence();
            end = entries.get(entries.size() - 1).getSequence();
            modified = true;
        }

        // dispatch
        var dispatchCount = 0;
        while (dataFile.canDispatch() && dispatchCount < batchSize) {

            // entry
            final var sequence = dataFile.getDispatchSequence() + 1;
            final WalEntry entry;
            if (sequence >= start && sequence <= end) {
                entry = entries.get((int) (sequence - start));
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