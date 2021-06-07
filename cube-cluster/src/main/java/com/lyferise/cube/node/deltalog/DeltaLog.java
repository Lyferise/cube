package com.lyferise.cube.node.deltalog;

public class DeltaLog {

    public DeltaLogRecordGroup read(final long logSequenceNumberStart, final long logSequenceNumberEnd) {
        throw new UnsupportedOperationException();
    }

    public void append(final DeltaLogRecord[] records, final int recordCount) {
    }
}