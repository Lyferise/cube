package com.lyferise.cube.node.websockets;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.WebSocket;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;

import static java.util.UUID.randomUUID;

@Slf4j
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

    public Session add(final WebSocket webSocket) {
        synchronized (lock) {
            final var key = randomUUID();
            final var session = new Session(key, webSocket);
            sessions.put(key, session);
            wekSockets.put(session.getWebSocket(), session);
            return session;
        }
    }

    public Session remove(final WebSocket webSocket) {
        synchronized (lock) {
            final var session = wekSockets.remove(webSocket);
            sessions.remove(session.getKey());
            return session;
        }
    }
}