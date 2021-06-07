package com.lyferise.cube.node.deltalog;

import lombok.Value;

import java.util.function.Consumer;

@Value
public class DeltaLogQuery {
    long logSequenceNumberStart;
    long logSequenceNumberEnd;
    Consumer<DeltaLogRecordGroup> handleDeltaLogRecordGroup;
}