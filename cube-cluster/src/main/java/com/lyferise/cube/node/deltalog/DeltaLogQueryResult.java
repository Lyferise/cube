package com.lyferise.cube.node.deltalog;

import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
public class DeltaLogQueryResult {
    UUID queryId;
    List<DeltaLogRecord> records;
}