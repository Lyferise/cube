package com.lyferise.cube.node.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.Data;
import lombok.SneakyThrows;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Data
public class NodeConfiguration {
    long nodeId;
    WebSocketsConfiguration webSockets = new WebSocketsConfiguration();
    DataConfiguration data = new DataConfiguration();
    List<ClusterConfiguration> cluster = new ArrayList<>();

    @SneakyThrows
    public static NodeConfiguration readConfiguration(final String path) {
        final var mapper = new ObjectMapper(new YAMLFactory());
        return mapper.readValue(new File(path), NodeConfiguration.class);
    }
}