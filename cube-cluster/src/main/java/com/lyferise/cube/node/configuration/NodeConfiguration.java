package com.lyferise.cube.node.configuration;

import lombok.Data;

@Data
public class NodeConfiguration {
    long nodeId;
    WebSocketsConfiguration webSockets = new WebSocketsConfiguration();
    WalConfiguration wal = new WalConfiguration();
}