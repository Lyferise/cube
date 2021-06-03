package com.lyferise.cube.crdt;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.lyferise.cube.crdt.Delta.randomize;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ReplicatedSetTest {

    @Test
    public void shouldReplicateSet() {

        final var set1 = new ReplicatedSet<String>();
        final var set2 = new ReplicatedSet<String>();

        final var addA = new ReplicatedSet.AddElement("A");
        final var addB = new ReplicatedSet.AddElement("B");
        final var addC = new ReplicatedSet.AddElement("C");

        final List<Mutator> mutators1 = asList(
                addA,
                addB,
                addA
        );

        final List<Mutator> mutators2 = asList(
                addA,
                addC,
                addC,
                addC
        );

        // mutate locally
        final var deltas1 = set1.mutate(mutators1);
        assertThat(set1.getValues(), containsInAnyOrder("A", "B"));

        final var deltas2 = set2.mutate(mutators2);
        assertThat(set2.getValues(), containsInAnyOrder("A", "C"));

        // eventual consistency
        set1.merge(randomize(deltas2));
        assertThat(set1.getValues(), containsInAnyOrder("A", "B", "C"));

        set2.merge(randomize(deltas1));
        assertThat(set2.getValues(), containsInAnyOrder("A", "B", "C"));
    }
}