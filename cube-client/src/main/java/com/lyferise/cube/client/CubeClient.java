package com.lyferise.cube.client;

import com.lyferise.cube.client.websockets.WebSocketsClient;
import lombok.SneakyThrows;

import java.net.URI;

public class CubeClient {
    private final WebSocketsClient client;

    @SneakyThrows
    public CubeClient(final String address) {
        client = new WebSocketsClient(new URI(address));
        if (!client.connectBlocking()) {
            throw new UnsupportedOperationException("Failed to connect to " + address);
        }
    }

    public void send(final byte[] data) {
        client.send(data);
    }

    @SneakyThrows
    public void close() {
        client.closeBlocking();
    }
}