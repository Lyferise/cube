package com.lyferise.cube.node.wal;

import com.lyferise.cube.node.configuration.WalConfiguration;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;

public class WalWorker extends Thread {
    private static final long NANOSECONDS_PER_MILLISECOND = 1_000_000;
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
        while (true) {
            wal.execute(this::writeAndOrDispatch);
            wal.getRingBuffer().notEmpty().awaitNanos(50 * NANOSECONDS_PER_MILLISECOND);
        }
    }

    private void writeAndOrDispatch() {

        // wal
        final var dataFile = wal.getDataFile();
        final var indexFile = wal.getIndexFile();
        final var dispatcher = wal.getDispatcher();

        // drain
        entries.clear();
        wal.getRingBuffer().drainTo(entries, batchSize);

        // write
        var start = 0L;
        var end = 0L;
        if (!entries.isEmpty()) {
            for (final var entry : entries) {
                indexFile.setPosition(entry.getSequence(), dataFile.write(entry));
            }
            start = entries.get(0).getSequence();
            end = entries.get(entries.size() - 1).getSequence();
        }
        wal.flush();

        // dispatch
        var dispatchCount = 0;
        while (dataFile.getDispatchSequence() < dataFile.getWriteSequence() && dispatchCount < batchSize) {

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
                return;
            }
            dataFile.setDispatched(entry);
            dispatchCount++;
        }
    }
}