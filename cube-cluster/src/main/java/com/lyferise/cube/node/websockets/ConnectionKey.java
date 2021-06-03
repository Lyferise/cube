package com.lyferise.cube.node.websockets;

import org.java_websocket.WebSocket;

public class ConnectionKey {

    public static String connectionKey(final WebSocket connection) {
        final var remoteSocket = connection.getRemoteSocketAddress();
        return remoteSocket.getAddress().getHostAddress() + ':' + remoteSocket.getPort();
    }
}