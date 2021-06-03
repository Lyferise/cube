package com.lyferise.cube.crdt;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.shuffle;

// A delta is the result of a local mutation to a CRDT that is broadcast to remote replicas.
public interface Delta {

    static List<Delta> randomize(final List<Delta> in) {
        final var copy = new ArrayList<Delta>();
        copy.addAll(in);
        copy.addAll(in);
        shuffle(copy);
        return copy;
    }
}