package com.lyferise.cube.node;

import java.util.UUID;

public interface MessagePublisher {

    void send(UUID sessionKey, byte[] data);
}