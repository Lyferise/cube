package com.lyferise.cube;

import com.lyferise.cube.configuration.NodeConfiguration;

import java.util.ArrayList;
import java.util.List;

public class LocalCluster {
    private final List<CubeNode> nodes = new ArrayList<>();

    public LocalCluster(final int nodeCount) {
        for (var i = 0; i < nodeCount; i++) {
            addNode();
        }
    }

    public CubeNode getNode(final int index) {
        return nodes.get(index);
    }

    public int getNodeCount() {
        return nodes.size();
    }

    private void addNode() {
        final var nodeId = 1782 + 913L * nodes.size();
        final var config = new NodeConfiguration();
        config.setNodeId(nodeId);
        config.setWalPath(".cube/node" + nodeId + "/wal.dat");
        final CubeNode node = new CubeNode(config);
        nodes.add(node);
    }
}