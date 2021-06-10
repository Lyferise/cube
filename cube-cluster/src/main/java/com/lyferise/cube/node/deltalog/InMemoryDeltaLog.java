package com.lyferise.cube.node.deltalog;

import java.util.ArrayList;
import java.util.List;

public class InMemoryDeltaLog implements DeltaLog {
    private final List<DeltaLogRecord> records = new ArrayList<>();

    @Override
    public long getHeadSequenceNumber() {
        return records.size();
    }

    @Override
    public long getCommitSequenceNumber() {
        return records.size();
    }

    @Override
    public void setCommitSequenceNumber(final long commitSequenceNumber) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DeltaLogRecordGroup read(final long logSequenceNumberStart, final long logSequenceNumberEnd) {
        return new DeltaLogRecordGroup(
                records.subList(
                        (int) (logSequenceNumberStart - 1),
                        (int) (logSequenceNumberEnd)));
    }

    @Override
    public void append(final DeltaLogAppendRequest appendRequest) {
        if (!appendRequest.isCommitted()) throw new UnsupportedOperationException();
        for (final DeltaLogRecord record : appendRequest.getRecords()) {
            append(record);
        }
    }

    @Override
    public void flush() {
    }

    private void append(final DeltaLogRecord record) {
        record.setLogSequenceNumber(records.size() + 1);
        records.add(record);
    }
}