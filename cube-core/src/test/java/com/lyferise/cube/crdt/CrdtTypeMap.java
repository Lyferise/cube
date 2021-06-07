package com.lyferise.cube.crdt;

import com.lyferise.cube.serialization.TypeMap;

public class CrdtTypeMap extends TypeMap {

    public CrdtTypeMap() {
        add("node-counter", ReplicatedCounter.NodeCounter.class);
    }
}