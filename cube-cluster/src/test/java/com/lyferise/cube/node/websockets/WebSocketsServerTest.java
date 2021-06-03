package com.lyferise.cube.node.websockets;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static com.lyferise.cube.internet.EphemeralPort.getEphemeralPort;
import static com.lyferise.cube.node.websockets.ServerState.STARTED;
import static com.lyferise.cube.node.websockets.ServerState.STOPPED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class WebSocketsServerTest {

    @Test
    @SneakyThrows
    public void shouldStartThenStopWebSocketsServers() {
        for (var i = 0; i < 3; i++) {

            // server
            final var port = getEphemeralPort();
            final var server = new WebSocketsServer(port);
            server.start();
            assertThat(server.getStartSignal().await(5000), is(equalTo(true)));
            assertThat(server.getState(), is(equalTo(STARTED)));

            // client
            final var address = new URI("ws://localhost:" + port);
            final var client = new WebSocketsClient(address);
            assertThat(client.connectBlocking(), is(equalTo(true)));

            // message
            client.send("Hola!!");

            // close
            client.closeBlocking();
            assertThat(client.isClosed(), is(true));

            // stop
            server.stop();
            assertThat(server.getState(), is(equalTo(STOPPED)));
        }
    }
}