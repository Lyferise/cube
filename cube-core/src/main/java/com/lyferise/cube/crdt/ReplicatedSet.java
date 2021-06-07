package com.lyferise.cube.crdt;

import com.lyferise.cube.serialization.BinaryReader;
import com.lyferise.cube.serialization.BinaryWriter;
import lombok.Value;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReplicatedSet<T> extends AbstractDeltaCrdt {
    private final Set<Object> set = new HashSet<>();

    @SuppressWarnings("unchecked")
    public List<T> getValues() {
        return (List<T>) ((AllElements) read(new GetAllElements())).getValues();
    }

    @Override
    public QueryResult read(final Query query) {
        if (!(query instanceof GetAllElements)) throw new UnsupportedOperationException();
        return new AllElements(new ArrayList<>(set));
    }

    @Override
    public Delta mutate(final Mutator mutator) {
        if (!(mutator instanceof AddElement)) throw new UnsupportedOperationException();
        final var addElement = (AddElement) mutator;
        set.add(addElement.getValue());
        return addElement;
    }

    @Override
    public void merge(final Delta delta) {
        if (!(delta instanceof AddElement)) throw new UnsupportedOperationException();
        set.add(((AddElement) delta).getValue());
    }

    public static class GetAllElements implements Query {
    }

    @Value
    public static class AllElements implements QueryResult {
        List<Object> values;
    }

    @Value
    public static class AddElement implements Mutator, Delta {
        Object value;

        @Override
        public void read(final BinaryReader reader) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void write(final BinaryWriter writer) {
            throw new UnsupportedOperationException();
        }
    }
}