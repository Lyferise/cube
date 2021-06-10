package com.lyferise.cube.node.deltalog;

import com.lyferise.cube.serialization.BinaryReader;
import com.lyferise.cube.serialization.BinaryWriter;
import com.lyferise.cube.serialization.DataOutputWriter;
import com.lyferise.cube.serialization.RandomAccessFileReader;
import lombok.SneakyThrows;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class DeltaLogDataFile {
    private final RandomAccessFile file;
    private final BinaryReader reader;
    private final BinaryWriter writer;
    private long headSequenceNumber;
    private long commitSequenceNumber;
    private long fileLength;

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
            file.writeLong(0);
            flush();
        } else {
            headSequenceNumber = file.readLong();
            commitSequenceNumber = file.readLong();
        }
        fileLength = file.length();
    }

    public long getHeadSequenceNumber() {
        return headSequenceNumber;
    }

    public long getCommitSequenceNumber() {
        return commitSequenceNumber;
    }

    @SneakyThrows
    public void setCommitSequenceNumber(final long commitSequenceNumber) {
        this.commitSequenceNumber = commitSequenceNumber;
        file.seek(8);
        file.writeLong(commitSequenceNumber);
    }

    @SneakyThrows
    public DeltaLogRecordGroup read(
            final long position,
            final long logSequenceNumberStart,
            final long logSequenceNumberEnd) {

        file.seek(position);
        final int recordCount = (int) (logSequenceNumberEnd - logSequenceNumberStart + 1);
        var records = new ArrayList<DeltaLogRecord>(recordCount);
        for (var i = 0; i < recordCount; i++) {
            final var record = new DeltaLogRecord();
            record.read(reader);
            records.add(record);
        }
        return new DeltaLogRecordGroup(records);
    }

    @SneakyThrows
    public long write(final DeltaLogRecord record, final boolean isCommitted) {

        // position
        final var position = fileLength;
        file.seek(fileLength);

        // record
        record.setLogSequenceNumber(++headSequenceNumber);
        record.write(writer);

        // header
        fileLength = file.getFilePointer();
        file.seek(0);
        file.writeLong(headSequenceNumber);
        if (isCommitted) file.writeLong(commitSequenceNumber = headSequenceNumber);
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