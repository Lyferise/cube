package com.lyferise.cube.crdt;

// https://arxiv.org/pdf/1603.01529.pdf
public interface DeltaCrdt {

    QueryResult read(Query query);

    Delta mutate(Mutator mutator);

    Iterable<Delta> mutate(Iterable<Mutator> mutators);

    void merge(Delta delta);

    void merge(Iterable<Delta> deltas);
}