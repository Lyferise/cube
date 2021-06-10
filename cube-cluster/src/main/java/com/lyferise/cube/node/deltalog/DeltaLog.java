package com.lyferise.cube.node.deltalog;

public interface DeltaLog {

    long getHeadSequenceNumber();

    long getCommitSequenceNumber();

    void setCommitSequenceNumber(long commitSequenceNumber);

    DeltaLogRecordGroup read(long logSequenceNumberStart, long logSequenceNumberEnd);

    void append(DeltaLogAppendRequest appendRequest);

    void flush();
}