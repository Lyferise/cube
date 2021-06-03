package com.lyferise.cube.node;

import org.junit.jupiter.api.Test;

public class ClusterIntegrationTest {

    @Test
    public void shouldReconnectClient() {
        try (final var cluster = new LocalCluster(1)) {
            final var client = cluster.connectToNode(0);
            client.close();
            client.reconnect();
            client.send(new byte[50]);
        }
    }

    @Test
    public void shouldStartThreeNodes() {
        final var nodeCount = 3;
        try (final var cluster = new LocalCluster(nodeCount)) {
            for (var i = 0; i < nodeCount; i++) {
                final var client = cluster.connectToNode(i);
                client.send(new byte[1000]);
                client.close();
            }
        }
    }
}