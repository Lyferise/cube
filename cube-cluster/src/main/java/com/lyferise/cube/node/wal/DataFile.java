package com.lyferise.cube.node.wal;

import com.lyferise.cube.serialization.BinaryReader;
import com.lyferise.cube.serialization.BinaryWriter;
import com.lyferise.cube.serialization.DataOutputWriter;
import com.lyferise.cube.serialization.RandomAccessFileReader;
import lombok.SneakyThrows;

import java.io.File;
import java.io.RandomAccessFile;

public class DataFile {
    private final RandomAccessFile file;
    private final BinaryReader reader;
    private final BinaryWriter writer;
    private long writeSequence;
    private long writePosition;
    private long dispatchSequence;

    @SneakyThrows
    public DataFile(final String path) {

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
            writeSequence = file.readLong();
            dispatchSequence = file.readLong();
        }
        writePosition = file.length();
    }

    public boolean canDispatch() {
        return dispatchSequence < writeSequence;
    }

    public long getWriteSequence() {
        return writeSequence;
    }

    public long getDispatchSequence() {
        return dispatchSequence;
    }

    @SneakyThrows
    public void setDispatched(final WalEntry entry) {
        if (entry.getSequence() > dispatchSequence) {
            file.seek(8);
            file.writeLong(entry.getSequence());
            dispatchSequence = entry.getSequence();
        }
    }

    @SneakyThrows
    public long write(final WalEntry entry) {

        // check position
        final var position = writePosition;
        file.seek(position);
        file.writeLong(writePosition);

        // entry
        entry.write(writer);

        // done
        writePosition = file.getFilePointer();
        writeSequence = entry.getSpacetimeId().getSequence();
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

    @SneakyThrows
    public WalEntry read(final long position) {

        // position
        file.seek(position);
        final var checkPosition = reader.readLong();
        if (position != checkPosition) throw new UnsupportedOperationException("WAL position check failed");

        // entry
        final var entry = new WalEntry();
        entry.read(reader);
        return entry;
    }
}