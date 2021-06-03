package com.lyferise.cube.node.websockets;

import com.lyferise.cube.internet.EndpointAddress;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.java_websocket.WebSocket;

import java.util.UUID;

@Value
@AllArgsConstructor
public class Session {
    UUID key;
    WebSocket webSocket;
    EndpointAddress address;

    public Session(final UUID key, final WebSocket webSocket) {
        this(key, webSocket, new EndpointAddress(webSocket.getRemoteSocketAddress()));
    }
}