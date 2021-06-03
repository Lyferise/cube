package com.lyferise.cube.node;

import com.lyferise.cube.node.configuration.NodeConfiguration;
import com.lyferise.cube.events.SpacetimeIdGenerator;
import com.lyferise.cube.node.websockets.WebSocketsServer;
import com.lyferise.cube.time.CubeClock;
import com.lyferise.cube.time.SystemClock;
import com.lyferise.cube.node.wal.WalEntry;
import com.lyferise.cube.node.wal.Wal;

public class CubeNode {
    private final Wal wal;
    private final SpacetimeIdGenerator spacetimeIdGenerator;
    private final WebSocketsServer webSocketsServer;

    public CubeNode(final NodeConfiguration config) {
        this(config, new SystemClock());
    }

    public CubeNode(final NodeConfiguration config, final CubeClock clock) {
        this.wal = new Wal(config.getWal(), this::dispatch);
        this.spacetimeIdGenerator = new SpacetimeIdGenerator(config.getNodeId(), clock);
        this.webSocketsServer = new WebSocketsServer(config.getWebSockets());
    }

    public void accept(final byte[] data) {
        final var spacetimeId = spacetimeIdGenerator.next();
        wal.write(new WalEntry(spacetimeId.getSequence(), data));
    }

    public void stop() {
        wal.close();
    }

    private void dispatch(final WalEntry entry) {
    }
}