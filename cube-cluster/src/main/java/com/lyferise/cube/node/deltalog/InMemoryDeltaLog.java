package com.lyferise.cube.node.deltalog;

import java.util.ArrayList;
import java.util.List;

public class InMemoryDeltaLog implements DeltaLog {
    private final List<DeltaLogRecord> records = new ArrayList<>();
    private long commitSequenceNumber;

    @Override
    public long getHeadSequenceNumber() {
        return records.size();
    }

    @Override
    public long getCommitSequenceNumber() {
        return commitSequenceNumber;
    }

    @Override
    public void setCommitSequenceNumber(final long commitSequenceNumber) {
        this.commitSequenceNumber = commitSequenceNumber;
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
        for (final DeltaLogRecord record : appendRequest.getRecords()) {
            append(record, appendRequest.isCommitted());
        }
    }

    @Override
    public void flush() {
    }

    private void append(final DeltaLogRecord record, final boolean isCommitted) {
        final var logSequenceNumber = records.size() + 1;
        record.setLogSequenceNumber(logSequenceNumber);
        records.add(record);
        if (isCommitted) commitSequenceNumber = logSequenceNumber;
    }
}