package com.lyferise.cube.crdt;

import com.lyferise.cube.serialization.BinaryReader;
import com.lyferise.cube.serialization.BinaryWriter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.HashMap;
import java.util.Map;

public class ReplicatedCounter extends AbstractDeltaCrdt {
    private final Map<Long, Long> nodeIdToCounter = new HashMap<>();

    public long get() {
        return ((CounterValue) read(new GetCounter())).getValue();
    }

    @Override
    public QueryResult read(final Query query) {
        if (!(query instanceof GetCounter)) throw new UnsupportedOperationException();
        return new CounterValue(nodeIdToCounter.values().stream().mapToLong(counter -> counter).sum());
    }

    @Override
    public Delta mutate(final Mutator mutator) {
        if (!(mutator instanceof Increment)) throw new UnsupportedOperationException();
        final var nodeId = ((Increment) mutator).getNodeId();
        final var existingCounter = nodeIdToCounter.get(nodeId);
        final var value = existingCounter == null ? 1 : existingCounter + 1;
        nodeIdToCounter.put(nodeId, value);
        return new NodeCounter(nodeId, value);
    }

    @Override
    public void merge(final Delta delta) {

        // delta
        if (!(delta instanceof NodeCounter)) throw new UnsupportedOperationException();
        final var nodeCounter = (NodeCounter) delta;
        final var nodeId = nodeCounter.getNodeId();
        final var value = nodeCounter.getValue();

        // merge
        final var existingCounter = nodeIdToCounter.get(nodeId);
        if (existingCounter == null || existingCounter < value) {
            nodeIdToCounter.put(nodeId, value);
        }
    }

    public static class GetCounter implements Query {
    }

    @Value
    public static class CounterValue implements QueryResult {
        long value;
    }

    @Value
    public static class Increment implements Mutator {
        long nodeId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NodeCounter implements Delta {
        long nodeId;
        long value;

        @Override
        public void read(final BinaryReader reader) {
            nodeId = reader.readLong();
            value = reader.readLong();
        }

        @Override
        public void write(final BinaryWriter writer) {
            writer.writeLong(nodeId);
            writer.writeLong(value);
        }
    }
}