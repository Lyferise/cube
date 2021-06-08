package com.lyferise.cube.node.deltalog;

import com.lyferise.cube.concurrency.Signal;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static com.lyferise.cube.components.ComponentState.STARTED;
import static com.lyferise.cube.components.ComponentState.STOPPED;
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
        final var deltaLog = new DeltaLog();
        final var agent = new DeltaLogAgent(
                deltaLog,
                BATCH_SIZE,
                queryResult -> {
                },
                recordGroup -> {
                });
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
        final var deltaLog = new DeltaLog();
        final var deltaLogAgent = new DeltaLogAgent(
                deltaLog,
                BATCH_SIZE,
                queryResult -> {
                    result.set(queryResult);
                    onRead.set();
                },
                recordGroup -> onWrite.set());

        // write
        final var record = new DeltaLogRecord(1, new byte[1000]);
        deltaLog.append(new DeltaLogRecordGroup(record));
        assertThat(onWrite.await(5000), is(true));

        // read
        final var queryId = randomUUID();
        final var query = new DeltaLogQuery(queryId, 1, 1);
        deltaLog.read(query);
        assertThat(onRead.await(5000), is(true));
        final var queryResult = result.get();
        assertThat(queryResult.getRecords().size(), is(equalTo(1)));

        // stop
        deltaLogAgent.stop();
    }
}