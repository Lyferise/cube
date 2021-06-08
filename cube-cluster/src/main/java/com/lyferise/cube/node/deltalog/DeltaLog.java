package com.lyferise.cube.node.deltalog;

public interface DeltaLog {

    DeltaLogRecordGroup read(final long logSequenceNumberStart, final long logSequenceNumberEnd);

    void append(final DeltaLogRecordGroup recordGroup);
}