package com.lyferise.cube.crdt;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

public class ReplicatedSetTest extends AbstractCrdtTest<ReplicatedSet<String>> {

    @Override
    protected ReplicatedSet<String> createCrdt() {
        return new ReplicatedSet<>();
    }

    @Override
    protected List<List<Mutator>> getMutators() {
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

        return asList(mutators1, mutators2);
    }

    @Override
    protected void verifyCrdtHasMutators1(final ReplicatedSet<String> crdt) {
        assertThat(crdt.getValues(), containsInAnyOrder("A", "B"));
    }

    @Override
    protected void verifyCrdtHasMutators2(final ReplicatedSet<String> crdt) {
        assertThat(crdt.getValues(), containsInAnyOrder("A", "C"));
    }

    @Override
    protected void verifyConvergence(final ReplicatedSet<String> crdt) {
        assertThat(crdt.getValues(), containsInAnyOrder("A", "B", "C"));
    }
}