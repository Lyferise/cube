package com.lyferise.cube.client.websockets;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

@Slf4j
public class WebSocketsClient extends WebSocketClient {

    public WebSocketsClient(final URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(final ServerHandshake handshakeData) {
        log.info("WebSocketsClient::onOpen");
    }

    @Override
    public void onMessage(final String message) {
        log.info("WebSocketsClient::onMessage");
    }

    @Override
    public void onClose(final int code, final String reason, final boolean remote) {
        log.info("WebSocketsClient::onClose");
    }

    @Override
    public void onError(final Exception e) {
        log.info("WebSocketsClient::onError");
    }
}