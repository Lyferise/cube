package com.lyferise.cube.serialization;

public interface BinaryWriter {

    void writeShort(int value);

    void writeInt(int value);

    void writeLong(long value);

    void write(String text);

    void write(byte[] data);
}