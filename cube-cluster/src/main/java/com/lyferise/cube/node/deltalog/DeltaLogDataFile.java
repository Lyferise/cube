package com.lyferise.cube.node.deltalog;

import com.lyferise.cube.serialization.BinaryReader;
import com.lyferise.cube.serialization.BinaryWriter;
import com.lyferise.cube.serialization.DataOutputWriter;
import com.lyferise.cube.serialization.RandomAccessFileReader;
import lombok.SneakyThrows;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import static com.lyferise.cube.events.SequenceNumber.verifySequenceNumber;

public class DeltaLogDataFile {
    private final RandomAccessFile file;
    private final BinaryReader reader;
    private final BinaryWriter writer;
    private long writeSequence;
    private long writePosition;

    @SneakyThrows
    public DeltaLogDataFile(final String path) {

        // file
        final var f = new File(path);
        final var newFile = f.createNewFile();
        file = new RandomAccessFile(f, "rw");
        reader = new RandomAccessFileReader(file);
        writer = new DataOutputWriter(file);

        // header
        if (newFile) {
            file.writeLong(0);
            flush();
        } else {
            writeSequence = file.readLong();
        }
        writePosition = file.length();
    }

    @SneakyThrows
    public DeltaLogRecordGroup read(
            final long position,
            final long logSequenceNumberStart,
            final long logSequenceNumberEnd) {

        file.seek(position);
        final int recordCount = (int) (logSequenceNumberEnd - logSequenceNumberStart + 1);
        final List<DeltaLogRecord> records = new ArrayList<>(recordCount);
        for (var i = 0; i < recordCount; i++) {
            final var record = new DeltaLogRecord();
            record.read(reader);
            records.add(record);
        }
        return new DeltaLogRecordGroup(records);
    }

    @SneakyThrows
    public long write(final DeltaLogRecord record) {

        // sequence
        verifySequenceNumber(record.getLogSequenceNumber(), writeSequence + 1);

        // position
        final var position = writePosition;
        file.seek(position);

        // record
        record.write(writer);

        // header
        writePosition = file.getFilePointer();
        writeSequence = record.getLogSequenceNumber();
        file.seek(0);
        file.writeLong(writeSequence);
        return position;
    }

    @SneakyThrows
    public void flush() {
        file.getChannel().force(true);
    }

    @SneakyThrows
    public void close() {
        file.close();
    }
}