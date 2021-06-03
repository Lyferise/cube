package com.lyferise.cube.node;

import com.lyferise.cube.node.configuration.NodeConfiguration;
import com.lyferise.cube.events.SpacetimeIdGenerator;
import com.lyferise.cube.node.websockets.ConnectionManager;
import com.lyferise.cube.node.websockets.WebSocketsServer;
import com.lyferise.cube.time.CubeClock;
import com.lyferise.cube.time.SystemClock;
import com.lyferise.cube.node.wal.WalEntry;
import com.lyferise.cube.node.wal.Wal;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CubeNode {
    private final NodeConfiguration config;
    private final WebSocketsServer webSocketsServer;
    private final Wal wal;

    public CubeNode(final NodeConfiguration config) {
        this(config, new SystemClock());
    }

    public CubeNode(final NodeConfiguration config, final CubeClock clock) {
        this.config = config;

        // wal
        this.wal = new Wal(config.getWal(), this::dispatch);
        final var sequence = wal.getDataFile().getWriteSequence();
        final var spacetimeIdGenerator = new SpacetimeIdGenerator(config.getNodeId(), clock, sequence);

        // start websockets
        final var connectionManager = new ConnectionManager();
        this.webSocketsServer = new WebSocketsServer(
                config.getWebSockets(),
                connectionManager,
                wal,
                spacetimeIdGenerator);
    }

    public NodeConfiguration getConfig() {
        return config;
    }

    public void stop() {
        webSocketsServer.stop();
        wal.close();
    }

    private void dispatch(final WalEntry entry) {
        log.info("dispatch {}", entry.getSequence());
    }
}