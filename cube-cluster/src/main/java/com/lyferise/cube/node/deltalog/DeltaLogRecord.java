package com.lyferise.cube.node.deltalog;

import lombok.Value;

@Value
public class DeltaLogRecord {
    long logSequenceNumber;
    byte[] data;
}