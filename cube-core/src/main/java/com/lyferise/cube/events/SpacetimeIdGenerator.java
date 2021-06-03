package com.lyferise.cube.events;

import com.lyferise.cube.time.CubeClock;
import com.lyferise.cube.time.SystemClock;

public class SpacetimeIdGenerator {
    private final long node;
    private final CubeClock clock;
    private long sequence;

    public SpacetimeIdGenerator(final long node) {
        this(node, new SystemClock(), 0);
    }

    public SpacetimeIdGenerator(final long node, final CubeClock clock, final long sequence) {
        this.node = node;
        this.clock = clock;
        this.sequence = sequence;
    }

    public SpacetimeId next() {
        return new SpacetimeId(node, ++sequence, clock.getMillisecondsSinceEpoch());
    }
}