package com.lyferise.cube.node.deltalog;

import com.lyferise.cube.serialization.BinaryReader;
import com.lyferise.cube.serialization.BinarySerializable;
import com.lyferise.cube.serialization.BinaryWriter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeltaLogRecord implements BinarySerializable {
    long logSequenceNumber;
    byte[] data;

    public DeltaLogRecord(final byte[] data) {
        this.data = data;
    }

    @Override
    public void read(final BinaryReader reader) {
        logSequenceNumber = reader.readLong();
        data = reader.readByteArray();
    }

    @Override
    public void write(final BinaryWriter writer) {
        writer.writeLong(logSequenceNumber);
        writer.write(data);
    }
}