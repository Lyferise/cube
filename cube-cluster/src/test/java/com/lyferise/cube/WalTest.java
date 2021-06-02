package com.lyferise.cube;

import com.lyferise.cube.node.configuration.WalConfiguration;
import com.lyferise.cube.node.wal.Wal;
import com.lyferise.cube.node.wal.WalEntry;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static java.nio.file.Files.deleteIfExists;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class WalTest {

    @Test
    public void shouldWriteThenReadWal() {

        // write
        final var wal = createNewWal();
        final var entryCount = 100;
        for (var i = 1; i <= entryCount; i++) {
            wal.write(new WalEntry(i, new byte[1000]));
        }
        wal.flush();

        // read
        for (var i = 1; i <= entryCount; i++) {
            assertThat(wal.read().getSequence(), is(equalTo((long) i)));
        }
    }

    @Test
    public void shouldRestoreWal() {

        // write
        final var wal = createNewWal();
        final var writeCount = 100;
        for (var i = 1; i <= writeCount; i++) {
            wal.write(new WalEntry(i, new byte[1000]));
        }
        wal.flush();

        // read
        final var readCount = 58;
        var sequence = 0L;
        for (var i = 1; i <= readCount; i++) {
            assertThat(wal.read().getSequence(), is(equalTo(++sequence)));
        }
        wal.flush();
        wal.close();

        // restore
        final var wal2 = getWal();
        for (var i = 1; i <= writeCount - readCount; i++) {
            assertThat(wal2.read().getSequence(), is(equalTo(++sequence)));
        }
    }

    private static Wal getWal() {
        return new Wal(getConfig());
    }

    private static Wal createNewWal() {
        final var config = getConfig();
        deleteWal(config);
        return new Wal(config);
    }

    @SneakyThrows
    private static void deleteWal(final WalConfiguration config) {
        deleteIfExists(Paths.get(config.getDataFile()));
        deleteIfExists(Paths.get(config.getIndexFile()));
    }

    private static WalConfiguration getConfig() {
        final var config = new WalConfiguration();
        config.setDataFile(".wal.dat");
        config.setIndexFile(".wal.idx");
        return config;
    }
}