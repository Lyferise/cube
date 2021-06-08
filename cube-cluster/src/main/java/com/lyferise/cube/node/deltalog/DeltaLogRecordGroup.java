package com.lyferise.cube.node.deltalog;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

import static java.util.Arrays.asList;

@Value
@AllArgsConstructor
public class DeltaLogRecordGroup {
    List<DeltaLogRecord> records;

    public DeltaLogRecordGroup(final DeltaLogRecord... records) {
        this.records = asList(records);
    }
}