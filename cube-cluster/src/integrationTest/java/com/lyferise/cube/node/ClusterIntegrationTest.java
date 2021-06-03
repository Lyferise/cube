package com.lyferise.cube.node;

import com.lyferise.cube.client.CubeClient;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class ClusterIntegrationTest {

    @Test
    public void shouldStartThreeNodes() {

        // cluster
        final var nodeCount = 3;
        final var cluster = new LocalCluster(nodeCount);
        assertThat(cluster.getNodeCount(), is(equalTo(nodeCount)));

        // clients
        for (var i = 0; i < 3; i++) {
            final var port = cluster.getNode(i).getConfig().getWebSockets().getPort();
            final var client = new CubeClient("ws://localhost:" + port);
            client.send(new byte[1000]);
            client.close();
        }

        // stop
        cluster.stop();
    }
}