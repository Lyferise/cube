package com.lyferise.cube.node;

import com.lyferise.cube.client.websockets.WebSocketsClient;
import com.lyferise.cube.node.configuration.WebSocketsConfiguration;
import com.lyferise.cube.node.websockets.WebSocketsServer;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static com.lyferise.cube.internet.EphemeralPort.getEphemeralPort;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class WebSocketsServerTest {

    @Test
    @SneakyThrows
    public void shouldStartThenStopWebSocketsServers() {
        for (var i = 0; i < 3; i++) {

            // config
            final var config = new WebSocketsConfiguration();
            config.setPort(getEphemeralPort());

            // server
            final var server = new WebSocketsServer(config);

            // client
            final var address = new URI("ws://localhost:" + config.getPort());
            final var client = new WebSocketsClient(address);
            assertThat(client.connectBlocking(), is(equalTo(true)));

            // message
            client.send("Hola!!");

            // close
            client.closeBlocking();
            assertThat(client.isClosed(), is(true));

            // stop
            server.stop();
        }
    }
}