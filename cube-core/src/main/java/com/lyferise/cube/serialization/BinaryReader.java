package com.lyferise.cube.serialization;

import com.lyferise.cube.events.SpacetimeId;

public interface BinaryReader {

    byte readByte();

    boolean readBoolean();

    int readShort();

    int readInt();

    long readLong();

    String readString();

    byte[] readByteArray();

    BinarySerializable readSerializable();

    Object readObject();

    SpacetimeId readSpacetimeId();
}