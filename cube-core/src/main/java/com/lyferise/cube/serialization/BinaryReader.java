package com.lyferise.cube.serialization;

public interface BinaryReader {

    int readShort();

    String readString();

    byte[] readByteArray();
}