package com.lyferise.cube.node.deltalog;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.lyferise.cube.math.RandomBytes.randomBytes;
import static java.nio.file.Files.delete;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class DeltaLogFileTest {

    @Test
    @SneakyThrows
    public void shouldWriteThenReadLogFile() {

        // records
        final var recordCount = 100L;
        final var records = new ArrayList<DeltaLogRecord>();
        for (var i = 0; i < recordCount; i++) {
            records.add(new DeltaLogRecord(randomBytes(1000)));
        }

        // file
        final var dataFile = ".delta.dat";
        final var indexFile = ".delta.idx";
        try {

            // write
            try (final var deltaLog = new DeltaLogFile(dataFile, indexFile)) {
                assertThat(deltaLog.getHeadSequenceNumber(), is(equalTo(0L)));
                assertThat(deltaLog.getCommitSequenceNumber(), is(equalTo(0L)));
                deltaLog.append(new DeltaLogAppendRequest(records, true));
                assertThat(deltaLog.getHeadSequenceNumber(), is(equalTo(recordCount)));
                assertThat(deltaLog.getCommitSequenceNumber(), is(equalTo(recordCount)));
            }

            // read
            try (final var deltaLog = new DeltaLogFile(dataFile, indexFile)) {
                assertThat(deltaLog.getHeadSequenceNumber(), is(equalTo(recordCount)));
                assertThat(deltaLog.getCommitSequenceNumber(), is(equalTo(recordCount)));
                for (var i = 0; i < recordCount; i++) {
                    final var logSequenceNumber = i + 1;
                    final List<DeltaLogRecord> records2 = deltaLog.read(logSequenceNumber, logSequenceNumber).getRecords();
                    assertThat(records2.size(), is(equalTo(1)));
                    final var record = records2.get(0);
                    assertThat(record.getLogSequenceNumber(), is(equalTo((long) logSequenceNumber)));
                    assertThat(record.getData(), is(equalTo(records.get(i).getData())));
                }
            }

        } finally {
            delete(Paths.get(dataFile));
            delete(Paths.get(indexFile));
        }
    }
}