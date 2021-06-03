package com.lyferise.cube.client.websockets;

import com.lyferise.cube.client.Dispatcher;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;

@Slf4j
public class WebSocketsClient extends WebSocketClient {
    private final Dispatcher dispatcher;

    public WebSocketsClient(final URI address, final Dispatcher dispatcher) {
        super(address);
        this.dispatcher = dispatcher;
    }

    @Override
    public void onOpen(final ServerHandshake handshakeData) {
    }

    @Override
    public void onMessage(final String message) {
    }

    @Override
    public void onMessage(final ByteBuffer buffer) {
        final byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        dispatcher.dispatch(data);
    }

    @Override
    public void onClose(final int code, final String reason, final boolean remote) {
    }

    @Override
    public void onError(final Exception e) {
        log.error("error", e);
    }
}