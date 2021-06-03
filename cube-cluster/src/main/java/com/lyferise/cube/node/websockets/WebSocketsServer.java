package com.lyferise.cube.node.websockets;

import com.lyferise.cube.concurrency.Signal;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

import static com.lyferise.cube.node.websockets.ServerState.*;

@Slf4j
public class WebSocketsServer extends WebSocketServer {
    private final Signal startSignal = new Signal();
    private ServerState state = CREATED;

    public WebSocketsServer(final int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onStart() {
        log.info("WebSockets server started on port {}", getPort());
        state = STARTED;
        startSignal.set();
    }

    public ServerState getState() {
        return state;
    }

    public Signal getStartSignal() {
        return startSignal;
    }

    @Override
    public void onOpen(final WebSocket connection, final ClientHandshake handshake) {
        log.info("WebSocketsServer::onOpen");
    }

    @Override
    public void onClose(final WebSocket connection, int code, final String reason, final boolean remote) {
        log.info("WebSocketsServer::onClose");
    }

    @Override
    public void onMessage(final WebSocket connection, final String message) {
        log.info("WebSocketsServer::onMessage: [" + message + "]");
    }

    @Override
    public void onError(final WebSocket connection, final Exception e) {

        // failed to start?
        if (state == CREATED) {
            state = FAILED_TO_START;
            startSignal.set();
            return;
        }

        log.info("WebSocketsServer::onError");
    }

    @Override
    @SneakyThrows
    public void stop() {
        log.info("Stopping WebSockets server");
        super.stop();
        log.info("WebSockets server stopped");
        state = STOPPED;
    }
}