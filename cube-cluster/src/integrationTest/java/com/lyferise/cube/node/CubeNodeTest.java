package com.lyferise.cube.node;

import org.junit.jupiter.api.Test;

public class CubeNodeTest {

    @Test
    public void shouldConnectToThreeCubeNodes() {
        final var nodeCount = 3;
        try (final var cluster = new LocalCluster(nodeCount)) {
            for (var i = 0; i < nodeCount; i++) {
                final var client = cluster.connectToNode(i, "test", "test");
                client.close();
            }
        }
    }

    @Test
    public void shouldReconnectToCubeNode() {
        try (final var cluster = new LocalCluster(1)) {
            final var client = cluster.connectToNode(0, "test", "test");
            client.close();
            client.reconnect("test", "test");
        }
    }
}