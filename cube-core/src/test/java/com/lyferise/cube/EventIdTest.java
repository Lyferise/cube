package com.lyferise.cube;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class EventIdTest {

    @Test
    public void shouldGetNodeAndSequence() {

        final var time = 67836382932L;
        final var node = 81723L;
        final var sequence = 9018902834912L;

        final var eventId = new EventId(node, sequence, time);
        assertThat(eventId.getSpace(), is(equalTo(1437695239010643680L)));
        assertThat(eventId.getTime(), is(equalTo(time)));
        assertThat(eventId.getNode(), is(equalTo(node)));
        assertThat(eventId.getSequence(), is(equalTo(sequence)));

        final var space = (node << 44) | sequence;

        assertThat(space >> 44, is(equalTo(node)));
        assertThat(space & 0xFFFFFFFFFFFL, is(equalTo(sequence)));
        assertThat(eventId.toString(), is(equalTo("81723.9018902834912@67836382932")));
    }
}