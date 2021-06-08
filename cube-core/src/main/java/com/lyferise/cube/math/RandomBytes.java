package com.lyferise.cube.math;

import java.util.Random;

public class RandomBytes {
    private static final Random RANDOM = new Random();

    public static byte[] randomBytes(final int length) {
        final var data = new byte[length];
        RANDOM.nextBytes(data);
        return data;
    }
}