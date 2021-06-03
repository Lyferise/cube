package com.lyferise.cube.node.websockets;

import org.java_websocket.WebSocket;

public class Connection {
    private final String key;
    private final WebSocket webSocket;

    public Connection(final String key, final WebSocket webSocket) {
        this.key = key;
        this.webSocket = webSocket;
    }

    public String getKey() {
        return key;
    }
}