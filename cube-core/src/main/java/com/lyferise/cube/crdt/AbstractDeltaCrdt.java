package com.lyferise.cube.crdt;

import java.util.ArrayList;

public abstract class AbstractDeltaCrdt implements DeltaCrdt {

    @Override
    public Iterable<Delta> mutate(final Iterable<Mutator> mutators) {
        final var list = new ArrayList<Delta>();
        for (final var mutator : mutators) {
            list.add(mutate(mutator));
        }
        return list;
    }

    @Override
    public void merge(final Iterable<Delta> deltas) {
        for (final var delta : deltas) {
            merge(delta);
        }
    }
}