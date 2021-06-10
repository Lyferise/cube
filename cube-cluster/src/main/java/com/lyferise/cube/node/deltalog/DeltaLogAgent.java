package com.lyferise.cube.node.deltalog;

import com.lyferise.cube.agents.Agent;
import com.lyferise.cube.concurrency.RingBuffer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

import static java.lang.Math.min;
import static java.lang.Thread.sleep;

@Slf4j
public class DeltaLogAgent extends Agent {
    private final RingBuffer<DeltaLogQuery> readQueue = new RingBuffer<>();
    private final RingBuffer<DeltaLogAppendRequest> appendQueue = new RingBuffer<>();
    private final Consumer<DeltaLogQueryResult> onRead;
    private final Consumer<DeltaLogAppendRequest> onAppend;
    private final DeltaLogProcessor deltaLogProcessor;
    private final DeltaLog deltaLog;
    private final int batchSize;

    public DeltaLogAgent(
            final DeltaLog deltaLog,
            final int batchSize,
            final Consumer<DeltaLogQueryResult> onRead,
            final Consumer<DeltaLogAppendRequest> onAppend,
            final DeltaLogProcessor deltaLogProcessor) {

        this.deltaLog = deltaLog;
        this.batchSize = batchSize;
        this.onRead = onRead;
        this.onAppend = onAppend;
        this.deltaLogProcessor = deltaLogProcessor;
    }

    @SneakyThrows
    public void enqueue(final DeltaLogQuery readQuery) {
        readQueue.put(readQuery);
    }

    @SneakyThrows
    public void enqueue(final DeltaLogAppendRequest appendRequest) {
        appendQueue.put(appendRequest);
    }

    @Override
    protected void execute() {

        // write
        var modified = appendToHead();
        modified |= commitFromTail();
        if (modified) deltaLog.flush();

        // read
        executeReadQueries();
    }

    @Override
    @SneakyThrows
    protected void waitForSignal() {

        // if there is nothing to read or write, wait 50 milliseconds
        if (readQueue.isEmpty()
                && appendQueue.isEmpty()
                && deltaLog.getHeadSequenceNumber() == deltaLog.getCommitSequenceNumber()) {
            sleep(50);
        }
    }

    @Override
    protected void onError(final Exception e) {
        log.error("Delta log agent error", e);
    }

    private boolean appendToHead() {
        var n = 0;
        DeltaLogAppendRequest appendRequest;
        while (n < batchSize && (appendRequest = appendQueue.poll()) != null) {
            deltaLog.append(appendRequest);
            onAppend.accept(appendRequest);
            n += appendRequest.getRecords().size();
        }
        return n > 0;
    }

    private boolean commitFromTail() {

        // commit?
        var modified = false;
        if (deltaLogProcessor.isProcessing()) return false;
        var commitSequenceNumber = deltaLogProcessor.takeCommitSequenceNumber();
        if (commitSequenceNumber != 0) {
            deltaLog.setCommitSequenceNumber(commitSequenceNumber);
            modified = true;
        } else {
            commitSequenceNumber = deltaLog.getCommitSequenceNumber();
        }

        // apply?
        final var headSequenceNumber = deltaLog.getHeadSequenceNumber();
        if (headSequenceNumber == commitSequenceNumber) return modified;
        final var logSequenceNumberStart = commitSequenceNumber + 1;
        final var logSequenceNumberEnd = min(commitSequenceNumber + batchSize, headSequenceNumber);
        deltaLogProcessor.apply(deltaLog.read(logSequenceNumberStart, logSequenceNumberEnd));
        return true;
    }

    private void executeReadQueries() {
        var n = 0;
        DeltaLogQuery query;
        while (n < batchSize && (query = readQueue.poll()) != null) {
            final var records = deltaLog
                    .read(query.getLogSequenceNumberStart(), query.getLogSequenceNumberEnd())
                    .getRecords();
            onRead.accept(new DeltaLogQueryResult(query.getQueryId(), records));
            n += records.size();
        }
    }
}