package com.lyferise.cube;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class ClusterIntegrationTest {

    @Test
    public void shouldStart3Nodes() {
        final var nodeCount = 3;
        final var cluster = new LocalCluster(nodeCount);
        assertThat(cluster.getNodeCount(), is(equalTo(nodeCount)));
    }
}