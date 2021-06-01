package com.lyferise.cube.events;

import com.lyferise.cube.time.CubeClock;
import com.lyferise.cube.time.SystemClock;

public class SpacetimeIdGenerator {
    private final long node;
    private final CubeClock clock;
    private long sequence;

    public SpacetimeIdGenerator(final long node) {
        this(node, new SystemClock());
    }

    public SpacetimeIdGenerator(final long node, final CubeClock clock) {
        this.node = node;
        this.clock = clock;
    }

    public SpacetimeId next() {
        return new SpacetimeId(node, ++sequence, clock.getMillisecondsSinceEpoch());
    }
}