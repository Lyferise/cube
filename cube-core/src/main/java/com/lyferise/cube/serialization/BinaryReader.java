package com.lyferise.cube.serialization;

public interface BinaryReader {

    boolean readBoolean();

    int readShort();

    int readInt();

    long readLong();

    String readString();

    byte[] readByteArray();

    BinarySerializable readSerializable();
}