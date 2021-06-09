package com.lyferise.cube.node;

import com.lyferise.cube.events.SpacetimeIdGenerator;
import com.lyferise.cube.node.configuration.NodeConfiguration;
import com.lyferise.cube.node.security.DefaultAuthenticator;
import com.lyferise.cube.node.websockets.SessionManager;
import com.lyferise.cube.node.websockets.WebSocketsServer;
import com.lyferise.cube.time.CubeClock;
import com.lyferise.cube.time.SystemClock;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CubeNode {
    private final NodeConfiguration config;
    private final WebSocketsServer webSocketsServer;

    public CubeNode(final NodeConfiguration config) {
        this(config, new SystemClock());
    }

    public CubeNode(final NodeConfiguration config, final CubeClock clock) {
        this.config = config;

        // dispatcher
        final var sessionManager = new SessionManager();
        final var authenticator = new DefaultAuthenticator(sessionManager);
        final var dispatcher = new Dispatcher(sessionManager, authenticator);

        // sequence
        final var spacetimeIdGenerator = new SpacetimeIdGenerator(config.getNodeId(), clock, 1);

        // start websockets
        this.webSocketsServer = new WebSocketsServer(
                config.getWebSockets(),
                sessionManager,
                dispatcher,
                spacetimeIdGenerator);
    }

    public NodeConfiguration getConfig() {
        return config;
    }

    public void stop() {
        webSocketsServer.stop();
    }
}