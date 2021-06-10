package com.lyferise.cube.node.deltalog;

public interface DeltaLogProcessor {

    void apply(DeltaLogRecordGroup recordGroup);

    boolean isProcessing();

    long takeCommitSequenceNumber();
}