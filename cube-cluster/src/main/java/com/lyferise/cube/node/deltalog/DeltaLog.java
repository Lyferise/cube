package com.lyferise.cube.node.deltalog;

import java.util.ArrayList;
import java.util.List;

public class DeltaLog {
    private final List<DeltaLogRecord> records = new ArrayList<>();

    public DeltaLogRecordGroup read(final DeltaLogQuery query) {
        throw new UnsupportedOperationException();
    }

    public void append(final DeltaLogRecordGroup recordGroup) {
        for (final DeltaLogRecord record : recordGroup.getRecords()) {
            append(record);
        }
    }

    private void append(final DeltaLogRecord record) {

        // sequence
        final var expectedLogSequenceNumber = records.size() + 1;
        final var logSequenceNumber = record.getLogSequenceNumber();
        if (logSequenceNumber != expectedLogSequenceNumber) {
            throw new UnsupportedOperationException(
                    "Delta log sequence check failed: expected "
                            + expectedLogSequenceNumber
                            + " not "
                            + logSequenceNumber
                            + ".");
        }

        // append
        records.add(record);
    }
}