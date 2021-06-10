package com.lyferise.cube.node.deltalog;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor
public class DeltaLogRecordGroup {
    List<DeltaLogRecord> records;
}