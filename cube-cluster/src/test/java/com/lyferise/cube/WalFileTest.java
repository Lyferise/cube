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
    public void shouldWriteToWalFile() {
        final var file = new File("wal.dat");
        try {
            file.delete();
            assertThat(file.exists(), is(equalTo(false)));

            // write 100 entries
            final var walFile = new WalFile(file);
            final var entryCount = 100;
            final var dataSize = 1000;
            for (var i = 1; i <= entryCount; i++) {
                walFile.append(new WalEntry(i, new byte[dataSize]));
            }

            // verify
            assertThat(file.length(), is(equalTo((long) (entryCount * (dataSize + 24)))));

        } finally {
            file.delete();
        }
    }
}