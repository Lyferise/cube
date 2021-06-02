package com.lyferise.cube;

import com.lyferise.cube.node.configuration.WalConfiguration;
import com.lyferise.cube.node.wal.WalEntry;
import com.lyferise.cube.node.wal.WalFile;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class WalFileTest {

    @Test
    public void shouldWriteThenReadWalFile() {

        // write
        final var walFile = createNewWalFile();
        final var entryCount = 100;
        for (var i = 1; i <= entryCount; i++) {
            walFile.write(new WalEntry(i, new byte[1000]));
        }
        walFile.flush();

        // read
        for (var i = 1; i <= entryCount; i++) {
            assertThat(walFile.read().getSequence(), is(equalTo((long) i)));
        }
    }

    @Test
    @Disabled
    public void shouldRestoreWalFile() {

        // write
        final var walFile = createNewWalFile();
        final var writeCount = 100;
        for (var i = 1; i <= writeCount; i++) {
            walFile.write(new WalEntry(i, new byte[1000]));
        }
        walFile.flush();

        // read
        final var readCount = 58;
        var sequence = 0L;
        for (var i = 1; i <= readCount; i++) {
            assertThat(walFile.read().getSequence(), is(equalTo(++sequence)));
        }
        walFile.flush();
        walFile.close();

        // restore
        final var walFile2 = getWalFile();
        for (var i = 1; i <= writeCount - readCount; i++) {
            assertThat(walFile2.read().getSequence(), is(equalTo(++sequence)));
        }
    }

    private static WalFile getWalFile() {
        return new WalFile(getConfig());
    }

    private static WalFile createNewWalFile() {
        final var config = getConfig();
        deleteWalFile(config);
        return new WalFile(config);
    }

    private static void deleteWalFile(final WalConfiguration config) {
        final var file = new File(config.getDataFile());
        file.delete();
        assertThat(file.exists(), is(equalTo(false)));
    }

    private static WalConfiguration getConfig() {
        final var config = new WalConfiguration();
        config.setDataFile(".wal");
        return config;
    }
}