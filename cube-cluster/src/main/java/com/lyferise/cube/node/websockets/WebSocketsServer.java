package com.lyferise.cube.node.websockets;

import com.lyferise.cube.components.ComponentState;
import com.lyferise.cube.concurrency.Signal;
import com.lyferise.cube.events.SpacetimeIdGenerator;
import com.lyferise.cube.node.configuration.WebSocketsConfiguration;
import com.lyferise.cube.node.wal.Wal;
import com.lyferise.cube.node.wal.WalEntry;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import static com.lyferise.cube.components.ComponentState.*;

@Slf4j
public class WebSocketsServer extends WebSocketServer {
    private final SessionManager sessionManager;
    private final Wal wal;
    private final SpacetimeIdGenerator spacetimeIdGenerator;
    private final Signal startSignal = new Signal();
    private ComponentState state = CREATED;

    public WebSocketsServer(
            final WebSocketsConfiguration config,
            final SessionManager sessionManager,
            final Wal wal,
            final SpacetimeIdGenerator spacetimeIdGenerator) {

        super(new InetSocketAddress(config.getPort()));
        this.sessionManager = sessionManager;
        this.wal = wal;
        this.spacetimeIdGenerator = spacetimeIdGenerator;
        startBlocking();
    }

    @Override
    public void onStart() {
        log.info("started on port {}", getPort());
        state = STARTED;
        startSignal.set();
    }

    @Override
    public void onOpen(final WebSocket webSocket, final ClientHandshake handshake) {
        final var session = sessionManager.add(webSocket);
        log.info("client {} connected", session.getAddress());
    }

    @Override
    public void onClose(final WebSocket webSocket, int code, final String reason, final boolean remote) {
        final var session = sessionManager.remove(webSocket);
        log.info("client {} disconnected", session.getAddress());
    }

    @Override
    public void onMessage(final WebSocket webSocket, final String message) {
        final var session = sessionManager.get(webSocket);
        log.info("client {} message {}", session.getAddress(), message);
    }

    @Override
    public void onMessage(final WebSocket webSocket, final ByteBuffer message) {
        final byte[] data = new byte[message.remaining()];
        message.get(data);
        wal.enqueue(new WalEntry(spacetimeIdGenerator.next(), data));
    }

    @Override
    public void onError(final WebSocket webSocket, final Exception e) {

        // failed to start?
        if (state == CREATED) {
            state = FAILED_TO_START;
            startSignal.set();
            return;
        }

        final var session = sessionManager.get(webSocket);
        log.error("client {} error", session.getAddress(), e);
    }

    @Override
    @SneakyThrows
    public void stop() {
        log.info("stopping");
        super.stop();
        log.info("stopped");
        state = STOPPED;
    }

    private void startBlocking() {
        start();

        if (!startSignal.await(5000) || state != STARTED) {
            throw new UnsupportedOperationException("Failed to start WebSockets server.");
        }
    }
}