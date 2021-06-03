package com.lyferise.cube.node.wal;

import com.lyferise.cube.concurrency.Signal;
import com.lyferise.cube.node.configuration.WalConfiguration;
import com.lyferise.cube.time.Timer;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.ArrayList;

import static java.nio.file.Files.deleteIfExists;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class WalTest {

    @Test
    @SneakyThrows
    public void shouldWriteThenReadWal() {

        // write
        final var entryCount = 100;
        final var signal = new Signal();
        final var entries = new ArrayList<WalEntry>();
        final var wal = createNewWal(e -> {
            entries.add(e);
            if (e.getSequence() == entryCount) signal.set();
        });
        for (var i = 1; i <= entryCount; i++) {
            wal.write(new WalEntry(i, new byte[1000]));
        }

        // verify
        assertThat(signal.await(5000), is(true));
        assertThat(entries.size(), is(equalTo(entryCount)));
        for (var i = 0; i < entryCount; i++) {
            assertThat(entries.get(i).getSequence(), is(equalTo(i + 1L)));
        }
    }

    @Test
    @SneakyThrows
    public void shouldRestoreWal() {

        // write
        final var timer = new Timer();
        final var entryCount = 100;
        final var signal = new Signal();
        final var entries = new ArrayList<WalEntry>();
        final var wal = createNewWal(e -> {
            if (e.getSequence() == 59) throw new UnsupportedOperationException();
            entries.add(e);
            if (e.getSequence() == 58) signal.set();
        });
        for (var i = 1; i <= entryCount; i++) {
            wal.write(new WalEntry(i, new byte[1000]));
        }

        // verify
        assertThat(signal.await(5000), is(true));
        wal.close();
        assertThat(entries.size(), is(equalTo(58)));
        for (var i = 0; i < 58; i++) {
            assertThat(entries.get(i).getSequence(), is(equalTo(i + 1L)));
        }

        // restore
        final var signal2 = new Signal();
        entries.clear();
        final var wal2 = getWal(e -> {
            entries.add(e);
            if (e.getSequence() == 100) signal2.set();
        });

        // verify
        timer.restart();
        assertThat(signal2.await(5000), is(true));
        wal2.close();
        assertThat(entries.size(), is(equalTo(42)));
        for (var i = 0; i < 42; i++) {
            assertThat(entries.get(i).getSequence(), is(equalTo(i + 59L)));
        }
    }

    private static Wal getWal(final WalDispatcher dispatcher) {
        return new Wal(getConfig(), dispatcher);
    }

    private static Wal createNewWal(final WalDispatcher dispatcher) {
        final var config = getConfig();
        deleteWal(config);
        return new Wal(config, dispatcher);
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