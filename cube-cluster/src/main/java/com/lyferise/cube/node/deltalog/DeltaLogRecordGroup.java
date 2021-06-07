package com.lyferise.cube.node.deltalog;

import lombok.Value;

import java.util.List;

@Value
public class DeltaLogRecordGroup {
    List<DeltaLogRecord> records;
}