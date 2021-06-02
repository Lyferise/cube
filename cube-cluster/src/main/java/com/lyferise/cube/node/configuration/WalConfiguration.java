package com.lyferise.cube.node.configuration;

import lombok.Data;

@Data
public class WalConfiguration {
    String path;
    int readQueueCapacity = 1024;
}