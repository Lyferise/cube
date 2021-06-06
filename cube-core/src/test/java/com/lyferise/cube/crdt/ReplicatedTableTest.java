package com.lyferise.cube.crdt;

import com.lyferise.cube.tables.Row;

import java.util.List;

import static com.lyferise.cube.events.SpacetimeId.parseSpacetimeId;
import static java.util.Arrays.asList;
import static java.util.Map.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class ReplicatedTableTest extends AbstractCrdtTest<ReplicatedTable> {

    @Override
    protected ReplicatedTable createCrdt() {
        return new ReplicatedTable("member_id");
    }

    @Override
    protected List<List<Mutator>> getMutators() {
        final var spacetimeId1 = parseSpacetimeId("81723.9018902834913@67836382933");
        final var spacetimeId2 = parseSpacetimeId("81723.9018902834913@67836382934");
        final var spacetimeId3 = parseSpacetimeId("81723.9018902834913@67836382935");

        final var memberId1 = parseSpacetimeId("79120.9018902834913@67836382933");
        final var memberId2 = parseSpacetimeId("91232.9018902834913@67836382932");

        final var upsert1 = new ReplicatedTable.UpsertRow(
                spacetimeId1,
                memberId1,
                new Row(of("email", "alice@culture", "name", "Alice")));

        final var upsert2 = new ReplicatedTable.UpsertRow(
                spacetimeId2,
                memberId2,
                new Row(of("email", "bob@culture", "name", "Bob")));

        final var upsert3 = new ReplicatedTable.UpsertRow(
                spacetimeId3,
                memberId2,
                new Row(of("email", "test@culture", "name", "Bob")));

        final List<Mutator> mutators1 = asList(
                upsert1,
                upsert2
        );

        final List<Mutator> mutators2 = asList(
                upsert2,
                upsert3,
                upsert3,
                upsert3
        );

        return asList(mutators1, mutators2);
    }

    @Override
    protected void verifyCrdtHasMutators1(final ReplicatedTable crdt) {
        final var df = crdt.select("email", "name");
        assertThat(df.getRowCount(), is(equalTo(2)));

        assertThat(
                df.contains(r -> r.get("email").equals("alice@culture") && r.get("name").equals("Alice")),
                is(equalTo(true)));
        assertThat(
                df.contains(r -> r.get("email").equals("bob@culture") && r.get("name").equals("Bob")),
                is(equalTo(true)));
    }

    @Override
    protected void verifyCrdtHasMutators2(final ReplicatedTable crdt) {
        final var df = crdt.select("email", "name");
        assertThat(df.getRowCount(), is(equalTo(1)));

        assertThat(
                df.contains(r -> r.get("email").equals("test@culture") && r.get("name").equals("Bob")),
                is(equalTo(true)));

    }

    @Override
    protected void verifyConvergence(final ReplicatedTable crdt) {
        final var df = crdt.select("email", "name");
        assertThat(df.getRowCount(), is(equalTo(2)));
        assertThat(
                df.contains(r -> r.get("email").equals("alice@culture") && r.get("name").equals("Alice")),
                is(equalTo(true)));
        assertThat(
                df.contains(r -> r.get("email").equals("test@culture") && r.get("name").equals("Bob")),
                is(equalTo(true)));
    }
}