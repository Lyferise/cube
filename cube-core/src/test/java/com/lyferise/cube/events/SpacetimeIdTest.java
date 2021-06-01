package com.lyferise.cube.events;

import com.lyferise.cube.time.SystemClock;
import org.junit.jupiter.api.Test;

import static com.lyferise.cube.events.SpacetimeId.generateSpacetimeId;
import static com.lyferise.cube.events.SpacetimeId.parseSpacetimeId;
import static com.lyferise.cube.time.CubeClock.toUTC;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SpacetimeIdTest {

    @Test
    public void shouldGetNodeAndSequence() {

        final var node = 81723L;
        final var sequence = 9018902834912L;
        final var time = 67836382932L;

        final var spacetimeId = new SpacetimeId(node, sequence, time);
        assertThat(spacetimeId.getSpace(), is(equalTo(1437695239010643680L)));
        assertThat(spacetimeId.getNode(), is(equalTo(node)));
        assertThat(spacetimeId.getSequence(), is(equalTo(sequence)));
        assertThat(spacetimeId.getTime(), is(equalTo(time)));

        final var space = (node << 44) | sequence;
        assertThat(space >> 44, is(equalTo(node)));
        assertThat(space & 0xFFFFFFFFFFFL, is(equalTo(sequence)));
        assertThat(spacetimeId.toString(), is(equalTo("81723.9018902834912@67836382932")));
    }

    @Test
    public void shouldParseSpacetimeId() {
        final var spacetimeId = parseSpacetimeId("81723.9018902834912@67836382932");
        assertThat(spacetimeId.getNode(), is(equalTo(81723L)));
        assertThat(spacetimeId.getSequence(), is(equalTo(9018902834912L)));
        assertThat(spacetimeId.getTime(), is(equalTo(67836382932L)));
    }

    @Test
    public void shouldGenerateSpacetimeId() {

        final var node = 81723L;
        final var sequence = 9018902834912L;
        final var clock = new SystemClock();

        final var spacetimeId = generateSpacetimeId(node, sequence, clock);
        assertThat(spacetimeId.getNode(), is(equalTo(node)));
        assertThat(spacetimeId.getSequence(), is(equalTo(sequence)));

        final var year = toUTC(spacetimeId.getTime()).getYear();
        assertThat(year, is(both(greaterThan(2020)).and(lessThan(2040))));
    }
}