package com.lyferise.cube.node.deltalog;

import com.lyferise.cube.agents.Agent;
import com.lyferise.cube.concurrency.RingBuffer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeltaLogAgent extends Agent {
    private final RingBuffer<DeltaLogQuery> readQueue = new RingBuffer<>();
    private final RingBuffer<DeltaLogRecord> appendQueue = new RingBuffer<>();
    private final DeltaLog deltaLog;
    private final int batchSize;
    private final DeltaLogRecord[] records;

    public DeltaLogAgent(final DeltaLog deltaLog, final int batchSize) {
        this.deltaLog = deltaLog;
        this.batchSize = batchSize;
        this.records = new DeltaLogRecord[batchSize];
    }

    @Override
    protected void execute() {

        // append
        final var recordCount = appendQueue.moveTo(records);
        deltaLog.append(records, recordCount);

        // TODO: read
    }

    @Override
    protected void waitForSignal() {
    }

    @Override
    protected void onError(final Exception e) {
        log.error("Delta log agent error", e);
    }
}