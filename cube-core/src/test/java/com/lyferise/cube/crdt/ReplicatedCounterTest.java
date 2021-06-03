package com.lyferise.cube.crdt;

import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class ReplicatedCounterTest {

    @Test
    public void shouldReplicateCounter() {

        final var counter1 = new ReplicatedCounter();
        final var counter2 = new ReplicatedCounter();

        final var nodeId1 = 8912;
        final var nodeId2 = 7901;

        final var incrementNode1 = new ReplicatedCounter.Increment(nodeId1);
        final var incrementNode2 = new ReplicatedCounter.Increment(nodeId2);

        final List<Mutator> mutators1 = asList(
                incrementNode1,
                incrementNode1,
                incrementNode2,
                incrementNode1,
                incrementNode2
        );

        final List<Mutator> mutators2 = asList(
                incrementNode2,
                incrementNode2,
                incrementNode2,
                incrementNode1
        );

        // mutate locally
        final var deltas1 = counter1.mutate(mutators1);
        assertThat(counter1.get(), is(equalTo(5L)));

        final var deltas2 = counter2.mutate(mutators2);
        assertThat(counter2.get(), is(equalTo(4L)));

        // eventual consistency
        counter1.merge(deltas2);
        assertThat(counter1.get(), is(equalTo(6L)));

        counter2.merge(deltas1);
        assertThat(counter2.get(), is(equalTo(6L)));
    }
}