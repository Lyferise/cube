package com.lyferise.cube.node;

import com.lyferise.cube.node.configuration.NodeConfiguration;
import com.lyferise.cube.events.SpacetimeIdGenerator;
import com.lyferise.cube.time.CubeClock;
import com.lyferise.cube.time.SystemClock;
import com.lyferise.cube.node.wal.WalEntry;
import com.lyferise.cube.node.wal.Wal;

public class CubeNode {
    private final Wal wal;
    private final SpacetimeIdGenerator spacetimeIdGenerator;

    public CubeNode(final NodeConfiguration config) {
        this(config, new SystemClock());
    }

    public CubeNode(final NodeConfiguration config, final CubeClock clock) {
        this.wal = new Wal(config.getWal(), e -> {
        });
        this.spacetimeIdGenerator = new SpacetimeIdGenerator(config.getNodeId(), clock);
    }

    public void accept(final byte[] data) {
        final var spacetimeId = spacetimeIdGenerator.next();
        wal.write(new WalEntry(spacetimeId.getSequence(), data));
    }
}