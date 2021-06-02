package com.lyferise.cube.node.wal;

@FunctionalInterface
public interface WalDispatcher {

    void dispatch(WalEntry entry);
}