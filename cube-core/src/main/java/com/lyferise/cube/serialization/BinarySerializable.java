package com.lyferise.cube.serialization;

public interface BinarySerializable {

    void read(BinaryReader reader);

    void write(BinaryWriter writer);
}