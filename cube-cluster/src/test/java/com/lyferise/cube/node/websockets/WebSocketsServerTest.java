package com.lyferise.cube.node.websockets;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static com.lyferise.cube.functions.Condition.waitFor;

public class WebSocketsServerTest {

    @Test
    @SneakyThrows
    public void shouldStartThenStopWebSocketsServer() {
        for (var i = 0; i < 3; i++) {
            final var server = new WebSocketsServer();
            server.start();
            waitFor(server::isStarted);
            server.stop();
        }
    }
}