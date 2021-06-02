package com.lyferise.cube;

import com.lyferise.cube.node.wal.WalEntry;
import com.lyferise.cube.node.wal.WalFile;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class WalFileTest {

    @Test
    public void shouldWriteThenReadWalFile() {
        final var file = new File(".wal");
        file.delete();
        assertThat(file.exists(), is(equalTo(false)));

        // write
        final var writer = new WalFile(file);
        final var entryCount = 100L;
        final var dataSize = 1000;
        for (var i = 1; i <= entryCount; i++) {
            writer.write(new WalEntry(i, new byte[dataSize]));
        }
        writer.flush();

        // read
        final var reader = new WalFile(file);
        WalEntry entry;
        var sequence = 0L;
        while ((entry = reader.next()) != null) {
            assertThat(entry.getSequence(), is(equalTo(++sequence)));
        }
        assertThat(sequence, is(equalTo(entryCount)));
    }
}