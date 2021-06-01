package com.lyferise.cube.events;

import org.junit.jupiter.api.Test;

import static com.lyferise.cube.events.EventId.parseEventId;
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
        assertThat(eventId.getNode(), is(equalTo(node)));
        assertThat(eventId.getSequence(), is(equalTo(sequence)));
        assertThat(eventId.getTime(), is(equalTo(time)));

        final var space = (node << 44) | sequence;

        assertThat(space >> 44, is(equalTo(node)));
        assertThat(space & 0xFFFFFFFFFFFL, is(equalTo(sequence)));
        assertThat(eventId.toString(), is(equalTo("81723.9018902834912@67836382932")));
    }

    @Test
    public void shouldParseEventId() {
        final var eventId = parseEventId("81723.9018902834912@67836382932");
        assertThat(eventId.getNode(), is(equalTo(81723L)));
        assertThat(eventId.getSequence(), is(equalTo(9018902834912L)));
        assertThat(eventId.getTime(), is(equalTo(67836382932L)));
    }
}