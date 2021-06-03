package com.lyferise.cube.node;

import com.lyferise.cube.client.CubeClient;
import com.lyferise.cube.node.configuration.WebSocketsConfiguration;
import com.lyferise.cube.node.websockets.WebSocketsServer;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static com.lyferise.cube.internet.EphemeralPort.getEphemeralPort;

public class WebSocketsServerTest {

    @Test
    @SneakyThrows
    public void shouldStartThenStopWebSocketsServers() {
        for (var i = 0; i < 3; i++) {

            // server
            final var config = new WebSocketsConfiguration();
            config.setPort(getEphemeralPort());
            final var server = new WebSocketsServer(config);

            // client
            final var client = new CubeClient("ws://localhost:" + config.getPort());
            client.send("Hola!!");
            client.close();

            // stop
            server.stop();
        }
    }
}