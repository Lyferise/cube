package com.lyferise.cube.node.deltalog;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static com.lyferise.cube.components.ComponentState.STARTED;
import static com.lyferise.cube.components.ComponentState.STOPPED;
import static java.lang.Thread.sleep;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class DeltaLogAgentTest {

    @Test
    @SneakyThrows
    public void shouldStartThenStopAgent() {

        // start
        final var deltaLog = new DeltaLog();
        final var batchSize = 1024;
        final Consumer<DeltaLogQueryResult> resultHandler = e -> {
        };
        final var agent = new DeltaLogAgent(deltaLog, batchSize, resultHandler);
        assertThat(agent.getState(), is(equalTo(STARTED)));

        // wait
        sleep(500);

        // stop
        agent.stop();
        assertThat(agent.getState(), is(equalTo(STOPPED)));
    }
}