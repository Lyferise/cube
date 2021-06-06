package com.lyferise.cube.crdt;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class ReplicatedCounterTest extends AbstractCrdtTest<ReplicatedCounter> {

    @Override
    protected ReplicatedCounter createCrdt() {
        return new ReplicatedCounter();
    }

    @Override
    protected List<List<Mutator>> getMutators() {
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

        return asList(mutators1, mutators2);
    }

    @Override
    protected void verifyCrdtHasMutators1(final ReplicatedCounter crdt) {
        assertThat(crdt.get(), is(equalTo(5L)));
    }

    @Override
    protected void verifyCrdtHasMutators2(final ReplicatedCounter crdt) {
        assertThat(crdt.get(), is(equalTo(4L)));
    }

    @Override
    protected void verifyConvergence(final ReplicatedCounter crdt) {
        assertThat(crdt.get(), is(equalTo(6L)));
    }
}