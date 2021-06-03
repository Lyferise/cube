package com.lyferise.cube.node;

import org.junit.jupiter.api.Test;

public class ClusterIntegrationTest {

    @Test
    public void shouldReconnectClient() {
        try (final var cluster = new LocalCluster(1)) {
            final var client = cluster.connectToNode(0, "test", "test");
            client.close();
            client.reconnect("test", "test");
        }
    }

    @Test
    public void shouldStartThreeNodes() {
        final var nodeCount = 3;
        try (final var cluster = new LocalCluster(nodeCount)) {
            for (var i = 0; i < nodeCount; i++) {
                final var client = cluster.connectToNode(i, "test", "test");
                client.close();
            }
        }
    }
}