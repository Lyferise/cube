package com.lyferise.cube.node.deltalog;

import java.util.ArrayList;
import java.util.List;

import static com.lyferise.cube.events.SequenceNumber.verifySequenceNumber;

public class InMemoryDeltaLog implements DeltaLog {
    private final List<DeltaLogRecord> records = new ArrayList<>();

    @Override
    public DeltaLogRecordGroup read(final long logSequenceNumberStart, final long logSequenceNumberEnd) {
        return new DeltaLogRecordGroup(
                records.subList(
                        (int) (logSequenceNumberStart - 1),
                        (int) (logSequenceNumberEnd)));
    }

    @Override
    public void append(final DeltaLogRecordGroup recordGroup) {
        for (final DeltaLogRecord record : recordGroup.getRecords()) {
            append(record);
        }
    }

    private void append(final DeltaLogRecord record) {
        verifySequenceNumber(record.getLogSequenceNumber(), records.size() + 1);
        records.add(record);
    }
}