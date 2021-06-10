package com.lyferise.cube.node.deltalog;

import com.lyferise.cube.concurrency.Signal;

import java.util.ArrayList;
import java.util.List;

public class FakeDeltaLogProcessor implements DeltaLogProcessor {
    private DeltaLogRecordGroup recordGroup;
    private final List<DeltaLogRecord> records = new ArrayList<>();
    private final Signal signal = new Signal();

    public Signal getSignal() {
        return signal;
    }

    public List<DeltaLogRecord> getRecords() {
        return records;
    }

    @Override
    public void apply(final DeltaLogRecordGroup recordGroup) {
        this.recordGroup = recordGroup;
        this.signal.set();
    }

    @Override
    public boolean isProcessing() {
        return false;
    }

    @Override
    public long takeCommitSequenceNumber() {
        if (recordGroup == null) return 0;
        final var records = recordGroup.getRecords();
        final var commitSequenceNumber = records.get(records.size() - 1).getLogSequenceNumber();
        this.records.addAll(recordGroup.getRecords());
        recordGroup = null;
        return commitSequenceNumber;
    }
}