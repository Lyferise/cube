package com.lyferise.cube.node.deltalog;

import com.lyferise.cube.concurrency.Signal;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import static com.lyferise.cube.components.ComponentState.STARTED;
import static com.lyferise.cube.components.ComponentState.STOPPED;
import static com.lyferise.cube.math.RandomBytes.randomBytes;
import static java.lang.Thread.sleep;
import static java.util.UUID.randomUUID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class DeltaLogAgentTest {
    private static final int BATCH_SIZE = 1024;

    @Test
    @SneakyThrows
    public void shouldStartThenStopAgent() {

        // start
        final var deltaLog = new InMemoryDeltaLog();
        final var agent = new DeltaLogAgent(
                deltaLog,
                BATCH_SIZE,
                queryResult -> {
                },
                appendRequest -> {
                },
                new FakeDeltaLogProcessor());
        assertThat(agent.getState(), is(equalTo(STARTED)));

        // wait
        sleep(500);

        // stop
        agent.stop();
        assertThat(agent.getState(), is(equalTo(STOPPED)));
    }

    @Test
    public void shouldAppendSingleRecord() {

        // concurrency
        final var onWrite = new Signal();
        final var onRead = new Signal();
        final var result = new AtomicReference<DeltaLogQueryResult>();

        // start
        final var deltaLog = new InMemoryDeltaLog();
        final var deltaLogAgent = new DeltaLogAgent(
                deltaLog,
                BATCH_SIZE,
                queryResult -> {
                    result.set(queryResult);
                    onRead.set();
                },
                appendRequest -> onWrite.set(),
                new FakeDeltaLogProcessor());

        // write
        final var data = randomBytes(1000);
        final var record = new DeltaLogRecord(data);
        deltaLogAgent.enqueue(new DeltaLogAppendRequest(true, record));
        assertThat(onWrite.await(5000), is(true));
        assertThat(deltaLog.getHeadSequenceNumber(), is(equalTo(1L)));
        assertThat(deltaLog.getCommitSequenceNumber(), is(equalTo(1L)));

        // read
        final var queryId = randomUUID();
        final var query = new DeltaLogQuery(queryId, 1, 1);
        deltaLogAgent.enqueue(query);
        assertThat(onRead.await(5000), is(true));
        final var queryResult = result.get();
        assertThat(queryResult.getQueryId(), is(equalTo(queryId)));
        assertThat(queryResult.getRecords().size(), is(equalTo(1)));
        final var record2 = queryResult.getRecords().get(0);
        assertThat(record2.getLogSequenceNumber(), is(equalTo(1L)));
        assertThat(record2.getData(), is(equalTo(data)));

        // stop
        deltaLogAgent.stop();
    }

    @Disabled
    @Test
    @SneakyThrows
    public void shouldReplayLog() {

        // records
        final var recordCount = 100L;
        final var records = new ArrayList<DeltaLogRecord>();
        for (var i = 0; i < recordCount; i++) {
            records.add(new DeltaLogRecord(randomBytes(1000)));
        }

        // delta log
        final var deltaLog = new InMemoryDeltaLog();
        deltaLog.append(new DeltaLogAppendRequest(records, false));

        // start
        final var deltaLogAgent = new DeltaLogAgent(
                deltaLog,
                BATCH_SIZE,
                queryResult -> {
                },
                appendRequest -> {
                },
                new FakeDeltaLogProcessor());

        // TODO: wait for signal!
        sleep(5000);

        // stop
        deltaLogAgent.stop();
    }
}