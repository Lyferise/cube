package com.lyferise.cube.node.websockets;

import org.java_websocket.WebSocket;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {
    private final Object lock = new Object();
    private final Map<UUID, Session> sessions = new HashMap<>();
    private final IdentityHashMap<WebSocket, Session> wekSockets = new IdentityHashMap<>();

    public Session get(final UUID key) {
        synchronized (lock) {
            return sessions.get(key);
        }
    }

    public Session get(final WebSocket webSocket) {
        synchronized (lock) {
            return wekSockets.get(webSocket);
        }
    }

    public void add(final Session session) {
        synchronized (lock) {
            sessions.put(session.getKey(), session);
            wekSockets.put(session.getWebSocket(), session);
        }
    }

    public void remove(final WebSocket webSocket) {
        synchronized (lock) {
            final var session = wekSockets.remove(webSocket);
            sessions.remove(session.getKey());
        }
    }
}