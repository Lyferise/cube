package com.lyferise.cube.time;

import org.junit.jupiter.api.Test;

import static com.lyferise.cube.time.CubeClock.toUTC;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SystemClockTest {

    @Test
    public void shouldGetMillisecondsSinceEpoch() {
        final var clock = new SystemClock();
        final var year = toUTC(clock.getMillisecondsSinceEpoch()).getYear();
        assertThat(year, is(both(greaterThan(2020)).and(lessThan(2040))));
    }
}