package com.lyferise.cube.node;

import com.lyferise.cube.node.configuration.NodeConfiguration;
import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.lyferise.cube.internet.EphemeralPort.getEphemeralPort;
import static java.nio.file.Files.*;
import static java.util.Comparator.reverseOrder;

public class LocalCluster {
    private final List<ClusterNode> nodes = new ArrayList<>();
    private final Path rootPath = Paths.get(".cube");

    @SneakyThrows
    public LocalCluster(final int nodeCount) {
        createRootPath();
        for (var i = 0; i < nodeCount; i++) {
            addNode();
        }
    }

    public CubeNode getNode(final int index) {
        return nodes.get(index).getCubeNode();
    }

    public int getNodeCount() {
        return nodes.size();
    }

    public void stop() {
        for (final var node : nodes) {
            node.getCubeNode().stop();
        }
    }

    @SneakyThrows
    private void addNode() {

        // config
        final var nodeId = 1782 + 913L * nodes.size();
        final var config = new NodeConfiguration();
        config.setNodeId(nodeId);
        config.getWebSockets().setPort(getEphemeralPort());

        // path
        final var nodePath = Paths.get(".cube/node" + nodeId);
        createDirectory(nodePath);

        // wal
        var wal = config.getWal();
        wal.setDataFile(nodePath.resolve("wal.dat").toString());
        wal.setIndexFile(nodePath.resolve("wal.idx").toString());

        // node
        nodes.add(new ClusterNode(new CubeNode(config), nodePath));
    }

    @SneakyThrows
    private void createRootPath() {

        // delete
        if (exists(rootPath)) {
            walk(rootPath)
                    .sorted(reverseOrder())
                    .forEach(path -> {
                        try {
                            delete(path);
                        } catch (final IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }

        // create
        createDirectory(rootPath);
    }
}