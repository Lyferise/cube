package com.lyferise.cube.crdt;

import java.util.List;

/*
// https://arxiv.org/pdf/1603.01529.pdf

A CRDT is a replicated set. Mathematically, the state of a delta CRDT is a join-semilattice; a partially ordered set
with a join operation (a least upper bound for any non-empty finite subset).

The join (+) of two elements in the CRDT is their least upper bound, according to the set's partial order. The join
operation of a CRDT must be associative, commutative and idempotent.

  associative: x + (y + z) = (x + y) + z
  commutative: x + y = y + x
  idempotent: x + x = x

For this reason, CRDT deltas must also be associative, commutative and idempotent:

  associative and commutative: deltas can be merged from different nodes in any order
  idempotent: duplicate deltas should not cause state changes
*/
public interface DeltaCrdt {

    QueryResult read(Query query);

    Delta mutate(Mutator mutator);

    List<Delta> mutate(Iterable<Mutator> mutators);

    void merge(Delta delta);

    void merge(Iterable<Delta> deltas);
}