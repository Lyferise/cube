package com.lyferise.cube.node.configuration;

import lombok.Data;

@Data
public class WalConfiguration {
    String dataFile;
    String indexFile;
    int dispatchQueueCapacity = 1024;
}