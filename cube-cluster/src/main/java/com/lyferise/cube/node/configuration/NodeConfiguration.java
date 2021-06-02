package com.lyferise.cube.node.configuration;

import lombok.Data;

@Data
public class NodeConfiguration {
    long nodeId;
    WalConfiguration wal = new WalConfiguration();
}