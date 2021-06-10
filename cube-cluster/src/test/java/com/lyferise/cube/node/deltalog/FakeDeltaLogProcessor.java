package com.lyferise.cube.node.deltalog;

public class FakeDeltaLogProcessor implements DeltaLogProcessor {

    @Override
    public void apply(final DeltaLogRecordGroup recordGroup) {
    }

    @Override
    public boolean isProcessing() {
        return false;
    }

    @Override
    public long takeCommitSequenceNumber() {
        return 0;
    }
}