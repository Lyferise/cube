package com.lyferise.cube.serialization;

public interface BinaryWriter {

    void writeShort(int value);

    void write(String text);

    void write(byte[] data);
}