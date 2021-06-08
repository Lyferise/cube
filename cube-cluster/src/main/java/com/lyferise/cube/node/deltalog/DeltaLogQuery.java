package com.lyferise.cube.node.deltalog;

import lombok.Value;

import java.util.UUID;

@Value
public class DeltaLogQuery {
    UUID queryId;
    long logSequenceNumberStart;
    long logSequenceNumberEnd;
}