package com.lyferise.cube.node;

import static com.lyferise.cube.node.configuration.NodeConfiguration.readConfiguration;

public class Application {

    public static void main(final String[] args) {
        try {
            final var config = readConfiguration("conf/application.yml");
            new CubeNode(config);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}