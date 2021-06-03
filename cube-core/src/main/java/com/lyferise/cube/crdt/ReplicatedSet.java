package com.lyferise.cube.crdt;

import lombok.Value;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;

public class ReplicatedSet<T> extends AbstractDeltaCrdt {
    private final Set<Object> set = new HashSet<>();

    @SuppressWarnings("unchecked")
    public List<T> getValues() {
        return asList((T[]) ((AllElements) read(new GetAllElements())).getValues());
    }

    @Override
    public QueryResult read(final Query query) {
        if (!(query instanceof GetAllElements)) throw new UnsupportedOperationException();
        return new AllElements(set.toArray());
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
        Object[] values;
    }

    @Value
    public static class AddElement implements Mutator, Delta {
        Object value;
    }
}