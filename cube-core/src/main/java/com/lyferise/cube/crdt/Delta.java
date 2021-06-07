package com.lyferise.cube.crdt;

import com.lyferise.cube.serialization.BinarySerializable;

// A delta is the result of a local mutation to a CRDT that is broadcast to remote replicas.
public interface Delta extends BinarySerializable {
}