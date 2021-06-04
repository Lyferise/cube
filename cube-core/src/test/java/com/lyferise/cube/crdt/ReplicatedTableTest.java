package com.lyferise.cube.crdt;

import com.lyferise.cube.tables.Row;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.lyferise.cube.crdt.Delta.randomize;
import static com.lyferise.cube.events.SpacetimeId.parseSpacetimeId;
import static java.util.Arrays.asList;
import static java.util.Map.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class ReplicatedTableTest {

    @Test
    public void shouldReplicateTable() {

        final var table1 = new ReplicatedTable("member_id");
        final var table2 = new ReplicatedTable("member_id");

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

        // mutate locally
        final var deltas1 = table1.mutate(mutators1);
        final var df1 = table1.select("email", "name");
        assertThat(df1.getRowCount(), is(equalTo(2)));
        assertThat(
                df1.contains(r -> r.get("email").equals("alice@culture") && r.get("name").equals("Alice")),
                is(equalTo(true)));
        assertThat(
                df1.contains(r -> r.get("email").equals("bob@culture") && r.get("name").equals("Bob")),
                is(equalTo(true)));

        final var deltas2 = table2.mutate(mutators2);
        final var df2 = table2.select("email", "name");
        assertThat(df2.getRowCount(), is(equalTo(1)));
        assertThat(
                df2.contains(r -> r.get("email").equals("test@culture") && r.get("name").equals("Bob")),
                is(equalTo(true)));

        // eventual consistency
        table1.merge(randomize(deltas2));
        final var df3 = table1.select("email", "name");
        assertThat(df3.getRowCount(), is(equalTo(2)));
        assertThat(
                df3.contains(r -> r.get("email").equals("alice@culture") && r.get("name").equals("Alice")),
                is(equalTo(true)));
        assertThat(
                df3.contains(r -> r.get("email").equals("test@culture") && r.get("name").equals("Bob")),
                is(equalTo(true)));

        table2.merge(randomize(deltas1));
        final var df4 = table2.select("email", "name");
        assertThat(df4.getRowCount(), is(equalTo(2)));
        assertThat(
                df4.contains(r -> r.get("email").equals("alice@culture") && r.get("name").equals("Alice")),
                is(equalTo(true)));
        assertThat(
                df4.contains(r -> r.get("email").equals("test@culture") && r.get("name").equals("Bob")),
                is(equalTo(true)));
    }
}