package com.lyferise.cube.node.configuration;

import org.junit.jupiter.api.Test;

import static com.lyferise.cube.node.configuration.NodeConfiguration.readConfiguration;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

public class ConfigurationTest {

    @Test
    public void shouldReadConfiguration() {
        final var configuration = readConfiguration("conf/application.yml");
        final var wsPort = configuration.getWebSockets().getPort();
        assertThat(wsPort, is(greaterThan(0)));
    }
}