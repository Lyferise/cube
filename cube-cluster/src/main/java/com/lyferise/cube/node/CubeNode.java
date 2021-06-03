package com.lyferise.cube.node;

import com.lyferise.cube.events.SpacetimeIdGenerator;
import com.lyferise.cube.node.configuration.NodeConfiguration;
import com.lyferise.cube.node.security.DefaultAuthenticator;
import com.lyferise.cube.node.wal.Wal;
import com.lyferise.cube.node.websockets.SessionManager;
import com.lyferise.cube.node.websockets.WebSocketsServer;
import com.lyferise.cube.time.CubeClock;
import com.lyferise.cube.time.SystemClock;
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

        // dispatcher
        final var sessionManager = new SessionManager();
        final var authenticator = new DefaultAuthenticator(sessionManager);
        final var dispatcher = new Dispatcher(sessionManager, authenticator);

        // wal
        this.wal = new Wal(config.getWal(), dispatcher);
        final var sequence = wal.getDataFile().getWriteSequence();
        final var spacetimeIdGenerator = new SpacetimeIdGenerator(config.getNodeId(), clock, sequence);

        // start websockets
        this.webSocketsServer = new WebSocketsServer(
                config.getWebSockets(),
                sessionManager,
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
}