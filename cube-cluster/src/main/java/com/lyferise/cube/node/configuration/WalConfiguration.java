package com.lyferise.cube.node.configuration;

import lombok.Data;

@Data
public class WalConfiguration {
    String dataFile;
    int readQueueCapacity = 1024;
}