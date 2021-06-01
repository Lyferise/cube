package com.lyferise.cube.events;

import org.junit.jupiter.api.Test;

import static com.lyferise.cube.time.CubeClock.toUTC;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SpacetimeIdGeneratorTest {

    @Test
    public void shouldGenerateSpacetimeIds() {

        final var node = 81723L;
        final var spacetimeIdGenerator = new SpacetimeIdGenerator(node);

        for (var i = 1L; i <= 5; i++) {
            final var spacetimeId = spacetimeIdGenerator.next();
            assertThat(spacetimeId.getNode(), is(equalTo(node)));
            assertThat(spacetimeId.getSequence(), is(equalTo(i)));

            final var year = toUTC(spacetimeId.getTime()).getYear();
            assertThat(year, is(both(greaterThan(2020)).and(lessThan(2040))));
        }
    }
}