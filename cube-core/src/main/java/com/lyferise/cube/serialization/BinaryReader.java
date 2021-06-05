package com.lyferise.cube.serialization;

public interface BinaryReader {

    int readShort();

    int readInt();

    long readLong();

    String readString();

    byte[] readByteArray();
}