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
        log.info("Started on port {}", getPort());
        state = STARTED;
        startSignal.set();
    }

    @Override
    public void onOpen(final WebSocket connection, final ClientHandshake handshake) {
        log.info("onOpen {}", connection.getRemoteSocketAddress());
    }

    @Override
    public void onClose(final WebSocket connection, int code, final String reason, final boolean remote) {
        log.info("onOpen {}", connection.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(final WebSocket connection, final String message) {
        log.info("onMessage {}" + message);
    }

    @Override
    public void onMessage(final WebSocket connection, final ByteBuffer message) {
        final byte[] data = new byte[message.remaining()];
        message.get(data);
        wal.write(new WalEntry(spacetimeIdGenerator.next(), data));
    }

    @Override
    public void onError(final WebSocket connection, final Exception e) {

        // failed to start?
        if (state == CREATED) {
            state = FAILED_TO_START;
            startSignal.set();
            return;
        }

        log.error("onError", e);
    }

    @Override
    @SneakyThrows
    public void stop() {
        log.info("Stopping");
        super.stop();
        log.info("Stopped");
        state = STOPPED;
    }

    private void startBlocking() {
        start();

        if (!startSignal.await(5000) || state != STARTED) {
            throw new UnsupportedOperationException("Failed to start WebSockets server.");
        }
    }
}