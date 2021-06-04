package com.lyferise.cube.events;

import org.junit.jupiter.api.Test;

import static com.lyferise.cube.events.SpacetimeId.parseSpacetimeId;
import static java.util.Arrays.asList;
import static java.util.Collections.sort;
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
    public void shouldGetHashCode() {
        final var spacetimeId1 = parseSpacetimeId("81723.9018902834913@67836382933");
        final var spacetimeId2 = parseSpacetimeId("81723.9018902834913@67836382932");
        final var spacetimeId3 = parseSpacetimeId("81723.9018902834912@67836382932");

        assertThat(spacetimeId1.hashCode(), is(equalTo(953243656)));
        assertThat(spacetimeId2.hashCode(), is(equalTo(953243657)));
        assertThat(spacetimeId3.hashCode(), is(equalTo(953243656)));
    }

    @Test
    public void shouldCompareSpacetimeIds() {
        final var spacetimeId1 = parseSpacetimeId("81723.9018902834913@67836382933");
        final var spacetimeId2 = parseSpacetimeId("81723.9018902834913@67836382932");
        final var spacetimeId3 = parseSpacetimeId("81723.9018902834912@67836382932");

        final var list = asList(spacetimeId1, spacetimeId2, spacetimeId3);
        sort(list);
        assertThat(list, contains(spacetimeId3, spacetimeId2, spacetimeId1));
    }
}