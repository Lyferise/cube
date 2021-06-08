package com.lyferise.cube.serialization;

import com.lyferise.cube.events.SpacetimeId;

public interface BinaryWriter {

    void writeBoolean(boolean value);

    void writeShort(int value);

    void writeInt(int value);

    void writeLong(long value);

    void write(String text);

    void write(byte[] data);

    void write(BinarySerializable value);

    void writeObject(Object value);

    void write(SpacetimeId spacetimeId);
}