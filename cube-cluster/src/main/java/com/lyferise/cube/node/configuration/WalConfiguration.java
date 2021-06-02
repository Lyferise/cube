package com.lyferise.cube.node.configuration;

import lombok.Data;

@Data
public class WalConfiguration {
    String dataFile;
    String indexFile;
    int ringBufferCapacity = 1024;
    int batchSize = 1000;
}