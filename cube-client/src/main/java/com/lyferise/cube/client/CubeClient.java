package com.lyferise.cube.client;

import com.lyferise.cube.client.websockets.WebSocketsClient;
import com.lyferise.cube.protocol.MessageWriter;
import lombok.SneakyThrows;

import java.net.URI;

import static com.lyferise.cube.protocol.MessageCode.AUTH;

public class CubeClient {
    private final WebSocketsClient client;

    @SneakyThrows
    public CubeClient(final String address) {
        client = new WebSocketsClient(new URI(address));
        if (!client.connectBlocking()) {
            throw new UnsupportedOperationException("Failed to connect to " + address);
        }
    }

    public void authenticate(final String email, final String password) {
        final MessageWriter writer = new MessageWriter(AUTH);
        writer.write(email);
        writer.write(password);
        client.send(writer.toByteArray());
    }

    @SneakyThrows
    public void reconnect() {
        client.reconnectBlocking();
    }

    @SneakyThrows
    public void close() {
        client.closeBlocking();
    }
}