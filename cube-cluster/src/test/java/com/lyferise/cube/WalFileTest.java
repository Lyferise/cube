package com.lyferise.cube;

import com.lyferise.cube.node.configuration.WalConfiguration;
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

        // config
        final var config = new WalConfiguration();
        config.setPath(".wal");

        // file
        final var file = new File(config.getPath());
        file.delete();
        assertThat(file.exists(), is(equalTo(false)));

        // write
        final var walFile = new WalFile(config);
        final var entryCount = 100;
        final var dataSize = 1000;
        for (var i = 1; i <= entryCount; i++) {
            walFile.write(new WalEntry(i, new byte[dataSize]));
        }
        walFile.flush();

        // read
        for (var i = 1; i <= entryCount; i++) {
            assertThat(walFile.read().getSequence(), is(equalTo((long) i)));
        }
    }
}