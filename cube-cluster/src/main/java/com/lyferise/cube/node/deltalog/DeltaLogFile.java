package com.lyferise.cube.node.deltalog;

import com.lyferise.cube.components.ComponentState;
import lombok.SneakyThrows;

import java.io.Closeable;

import static com.lyferise.cube.components.ComponentState.CREATED;
import static com.lyferise.cube.components.ComponentState.STOPPED;

public class DeltaLogFile implements DeltaLog, Closeable {
    private final DeltaLogDataFile dataFile;
    private final DeltaLogIndexFile indexFile;
    private ComponentState state = CREATED;

    public DeltaLogFile(final String dataFile, final String indexFile) {
        this.dataFile = new DeltaLogDataFile(dataFile);
        this.indexFile = new DeltaLogIndexFile(indexFile);
    }

    @Override
    public long getHeadSequenceNumber() {
        return dataFile.getHeadSequenceNumber();
    }

    @Override
    public long getCommitSequenceNumber() {
        return dataFile.getCommitSequenceNumber();
    }

    @Override
    public void setCommitSequenceNumber(final long commitSequenceNumber) {
        dataFile.setCommitSequenceNumber(commitSequenceNumber);
    }

    @Override
    public DeltaLogRecordGroup read(final long logSequenceNumberStart, final long logSequenceNumberEnd) {
        final var position = indexFile.getPosition(logSequenceNumberStart);
        return dataFile.read(position, logSequenceNumberStart, logSequenceNumberEnd);
    }

    @Override
    public void append(final DeltaLogAppendRequest appendRequest) {
        final var isCommitted = appendRequest.isCommitted();
        for (final DeltaLogRecord record : appendRequest.getRecords()) {
            final var position = dataFile.write(record, isCommitted);
            indexFile.setPosition(record.getLogSequenceNumber(), position);
        }
    }

    @Override
    @SneakyThrows
    public void flush() {
        if (state == STOPPED) return;
        dataFile.flush();
        indexFile.flush();
    }

    @Override
    @SneakyThrows
    public void close() {
        if (state == STOPPED) return;
        state = STOPPED;
        flush();
        dataFile.close();
        indexFile.close();
    }
}