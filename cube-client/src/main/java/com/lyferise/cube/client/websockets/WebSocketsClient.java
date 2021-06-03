package com.lyferise.cube.client.websockets;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

@Slf4j
public class WebSocketsClient extends WebSocketClient {

    public WebSocketsClient(final URI address) {
        super(address);
    }

    @Override
    public void onOpen(final ServerHandshake handshakeData) {
        log.info("onOpen");
    }

    @Override
    public void onMessage(final String message) {
        log.info("onMessage {}", message);
    }

    @Override
    public void onClose(final int code, final String reason, final boolean remote) {
        log.info("onClose");
    }

    @Override
    public void onError(final Exception e) {
        log.error("onError", e);
    }
}