package com.lyferise.cube.node.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.Data;
import lombok.SneakyThrows;

import java.io.File;

@Data
public class NodeConfiguration {
    long nodeId;
    WebSocketsConfiguration webSockets = new WebSocketsConfiguration();
    WalConfiguration wal = new WalConfiguration();

    @SneakyThrows
    public static NodeConfiguration readConfiguration(final String path) {
        final var mapper = new ObjectMapper(new YAMLFactory());
        return mapper.readValue(new File(path), NodeConfiguration.class);
    }
}