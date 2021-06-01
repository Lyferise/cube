package com.lyferise.cube.time;

@FunctionalInterface
public interface CubeClock {

    long getMillisecondsSinceEpoch();
}