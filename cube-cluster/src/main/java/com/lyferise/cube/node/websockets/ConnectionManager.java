package com.lyferise.cube.node.websockets;

import java.util.HashMap;
import java.util.Map;

public class ConnectionManager {
    private final Object lock = new Object();
    private final Map<String, Connection> connections = new HashMap<>();

    public Connection get(final String key) {
        synchronized (lock) {
            return connections.get(key);
        }
    }

    public void add(final Connection connection) {
        synchronized (lock) {
            connections.put(connection.getKey(), connection);
        }
    }

    public void remove(final String key) {
        synchronized (lock) {
            connections.remove(key);
        }
    }
}