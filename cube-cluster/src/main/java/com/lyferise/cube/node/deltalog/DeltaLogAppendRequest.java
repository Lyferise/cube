package com.lyferise.cube.node.deltalog;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

import static java.util.Arrays.asList;

@Value
@AllArgsConstructor
public class DeltaLogAppendRequest {
    List<DeltaLogRecord> records;
    boolean isCommitted;
    
    public DeltaLogAppendRequest(final boolean isCommitted, final DeltaLogRecord... records) {
        this.records = asList(records);
        this.isCommitted = isCommitted;
    }
}