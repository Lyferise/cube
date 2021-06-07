package com.lyferise.cube.crdt;

import com.lyferise.cube.serialization.ByteArrayReader;
import com.lyferise.cube.serialization.ByteArrayWriter;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.shuffle;

public abstract class AbstractCrdtTest<T extends DeltaCrdt> {

    @Test
    public void shouldReplicateOverTwoNodes() {

        // two replicas
        final var crdt1 = createCrdt();
        final var crdt2 = createCrdt();

        // mutators
        final var mutators = getMutators();

        // mutate locally
        final var deltas1 = crdt1.mutate(mutators.get(0));
        verifyCrdtHasMutators1(crdt1);

        final var deltas2 = crdt2.mutate(mutators.get(1));
        verifyCrdtHasMutators2(crdt2);

        // eventual consistency
        crdt1.merge(randomize(deltas2));
        crdt2.merge(randomize(deltas1));
        verifyConvergence(crdt1);
        verifyConvergence(crdt2);
    }

    protected abstract T createCrdt();

    protected abstract List<List<Mutator>> getMutators();

    protected abstract void verifyCrdtHasMutators1(T crdt);

    protected abstract void verifyCrdtHasMutators2(T crdt);

    protected abstract void verifyConvergence(T crdt);

    private static List<Delta> randomize(final List<Delta> deltas) {

        // duplicate
        final var copy = new ArrayList<Delta>();
        copy.addAll(deltas);
        copy.addAll(deltas);

        // shuffle
        shuffle(copy);

        // deserialize
        final var typeMap = new CrdtTypeMap();
        final var writer = new ByteArrayWriter(typeMap);
        for (final var delta : copy) {
            writer.write(delta);
        }
        final var reader = new ByteArrayReader(typeMap, writer.toByteArray());
        final var deserialized = new ArrayList<Delta>();
        for (var i = 0; i < copy.size(); i++) {
            deserialized.add((Delta) reader.readSerializable());
        }
        return deserialized;
    }
}