package com.lyferise.cube.time;

public class Timer {
    private final CubeClock clock;
    private long start;

    public Timer() {
        this(new SystemClock());
        restart();
    }

    public Timer(final CubeClock clock) {
        this.clock = clock;
    }

    public void restart() {
        start = clock.getMillisecondsSinceEpoch();
    }

    public long getElapsedMilliseconds() {
        return clock.getMillisecondsSinceEpoch() - start;
    }
}