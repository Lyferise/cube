package com.lyferise.cube.node.websockets;

import com.lyferise.cube.concurrency.Signal;
import com.lyferise.cube.events.SpacetimeIdGenerator;
import com.lyferise.cube.node.ComponentState;
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

import static com.lyferise.cube.node.ComponentState.*;
import static com.lyferise.cube.node.websockets.ConnectionKey.connectionKey;

@Slf4j
public class WebSocketsServer extends WebSocketServer {
    private final Wal wal;
    private final SpacetimeIdGenerator spacetimeIdGenerator;
    private final Signal startSignal = new Signal();
    private ComponentState state = CREATED;

    public WebSocketsServer(
            final WebSocketsConfiguration config,
            final Wal wal,
            final SpacetimeIdGenerator spacetimeIdGenerator) {

        super(new InetSocketAddress(config.getPort()));
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
    public void onOpen(final WebSocket connection, final ClientHandshake handshake) {
        final var connectionKey = connectionKey(connection);
        log.info("client {} connected", connectionKey);
    }

    @Override
    public void onClose(final WebSocket connection, int code, final String reason, final boolean remote) {
        final var connectionKey = connectionKey(connection);
        log.info("client {} disconnected", connectionKey);
    }

    @Override
    public void onMessage(final WebSocket connection, final String message) {
        final var connectionKey = connectionKey(connection);
        log.info("client {} message {}", connectionKey, message);
    }

    @Override
    public void onMessage(final WebSocket connection, final ByteBuffer message) {
        final byte[] data = new byte[message.remaining()];
        message.get(data);
        wal.enqueue(new WalEntry(spacetimeIdGenerator.next(), data));
    }

    @Override
    public void onError(final WebSocket connection, final Exception e) {

        // failed to start?
        if (state == CREATED) {
            state = FAILED_TO_START;
            startSignal.set();
            return;
        }

        final var connectionKey = connectionKey(connection);
        log.error("client {} error", connectionKey, e);
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