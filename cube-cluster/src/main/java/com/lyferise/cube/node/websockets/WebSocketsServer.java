package com.lyferise.cube.node.websockets;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

@Slf4j
public class WebSocketsServer extends WebSocketServer {
    private boolean started;

    @Override
    public void onStart() {
        log.info("WebSockets server started on port {}", getPort());
        started = true;
    }

    public boolean isStarted() {
        return started;
    }

    @Override
    public void onOpen(final WebSocket connection, final ClientHandshake handshake) {
        System.err.println("WebSocketsServer::onOpen");
    }

    @Override
    public void onClose(final WebSocket connection, int code, final String reason, final boolean remote) {
        System.err.println("WebSocketsServer::onClose");
    }

    @Override
    public void onMessage(final WebSocket connection, final String message) {
        System.err.println("WebSocketsServer::onMessage");
    }

    @Override
    public void onError(final WebSocket connection, final Exception e) {
        System.err.println("WebSocketsServer::onError");
    }
}