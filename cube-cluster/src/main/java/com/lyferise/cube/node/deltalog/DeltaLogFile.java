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
    public DeltaLogRecordGroup read(final long logSequenceNumberStart, final long logSequenceNumberEnd) {
        final var position = indexFile.getPosition(logSequenceNumberStart);
        return dataFile.read(position, logSequenceNumberStart, logSequenceNumberEnd);
    }

    @Override
    public void append(final DeltaLogRecordGroup recordGroup) {
        for (final DeltaLogRecord record : recordGroup.getRecords()) {
            indexFile.setPosition(record.getLogSequenceNumber(), dataFile.write(record));
        }
    }

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