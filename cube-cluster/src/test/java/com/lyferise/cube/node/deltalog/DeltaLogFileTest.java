package com.lyferise.cube.node.deltalog;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static com.lyferise.cube.math.RandomBytes.randomBytes;

public class DeltaLogFileTest {

    @Test
    public void shouldWriteThenReadLogFile() {

        // records
        final var recordCount = 100;
        final var records = new ArrayList<DeltaLogRecord>();
        for (var i = 0; i < recordCount; i++) {
            records.add(new DeltaLogRecord(i + 1, randomBytes(1000)));
        }

        // write
        try (final var deltaLog = new DeltaLogFile(".delta.dat", ".delta.idx")) {
            deltaLog.append(new DeltaLogRecordGroup(records));
        }
    }
}