package com.lyferise.cube.node;

import lombok.Value;

import java.nio.file.Path;

@Value
public class ClusterNode {
    CubeNode cubeNode;
    Path nodePath;
}