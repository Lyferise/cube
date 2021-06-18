package com.lyferise.cube.node.configuration;

import lombok.Data;
import java.util.List;

@Data
public class ClusterConfiguration {
    List<Cluster> clusters;

    public static class Cluster {
        int nodeId;
        String address;
    }
}
