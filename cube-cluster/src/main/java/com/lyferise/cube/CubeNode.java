package com.lyferise.cube;

import com.lyferise.cube.configuration.NodeConfiguration;
import com.lyferise.cube.events.SpacetimeIdGenerator;
import com.lyferise.cube.time.CubeClock;
import com.lyferise.cube.time.SystemClock;
import com.lyferise.cube.wal.WalEntry;
import com.lyferise.cube.wal.WalFile;

public class CubeNode {
    private final WalFile walFile;
    private final SpacetimeIdGenerator spacetimeIdGenerator;

    public CubeNode(final NodeConfiguration config) {
        this(config, new SystemClock());
    }

    public CubeNode(final NodeConfiguration config, final CubeClock clock) {
        this.walFile = new WalFile(config.getWalPath());
        this.spacetimeIdGenerator = new SpacetimeIdGenerator(config.getNodeId(), clock);
    }

    public void accept(final byte[] data) {
        final var spacetimeId = spacetimeIdGenerator.next();
        walFile.append(new WalEntry(spacetimeId.getSequence(), data));
    }
}