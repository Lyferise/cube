package com.lyferise.cube.time;

import static java.lang.System.currentTimeMillis;

public class SystemClock implements CubeClock {

    @Override
    public long getMillisecondsSinceEpoch() {
        return currentTimeMillis() - CUBE_EPOCH;
    }
}