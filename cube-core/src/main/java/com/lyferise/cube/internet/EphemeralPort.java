package com.lyferise.cube.internet;

import lombok.SneakyThrows;

import java.net.ServerSocket;

/*
This class returns ephemeral ports, used to support allocating endpoint addresses automatically. Note that
sockets may not be released until TIME_WAIT is passed. On macOS, the default timeout for TIME_WAIT is 15 seconds.
You can see which sockets are in TIME_WAIT state using netstat:

netstat -ant | grep TIME_WAIT
*/
public class EphemeralPort {

    @SneakyThrows
    public static int getEphemeralPort() {
        try (final var socket = new ServerSocket(0)) {
            socket.setReuseAddress(true);
            return socket.getLocalPort();
        }
    }
}