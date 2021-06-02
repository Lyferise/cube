package com.lyferise.cube.node.wal;

import lombok.SneakyThrows;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.lyferise.cube.node.wal.IndexPage.PAGE_SIZE;
import static java.lang.Math.min;

public class IndexFile {
    private static final long MAX_PAGE_COUNT = 1000;
    private long entryCount;
    private final RandomAccessFile file;

    private final Map<Integer, IndexPage> pages = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(final Map.Entry<Integer, IndexPage> eldest) {
            return size() > MAX_PAGE_COUNT;
        }
    };

    @SneakyThrows
    public IndexFile(final String path) {

        // file
        final var f = new File(path);
        final var newFile = f.createNewFile();
        file = new RandomAccessFile(f, "rw");

        // header
        if (newFile) {
            file.writeLong(0);
        } else {
            entryCount = file.readLong();
        }
    }

    @SneakyThrows
    public long getPosition(final long sequence) {
        var page = getPage(sequence);
        if (page == null) page = loadPage(sequence);
        return page.getPosition(sequence);
    }

    @SneakyThrows
    public void setPosition(final long sequence, final long position) {

        // index
        if (sequence != entryCount + 1) {
            throw new UnsupportedOperationException(
                    "WAL index check failed: next entry sequence = " + sequence
                            + ", index count = " + entryCount);
        }
        file.seek(getIndexPosition(sequence));
        file.writeLong(position);

        // header
        file.seek(0);
        file.writeLong(++entryCount);

        // page
        final var page = getPage(sequence);
        if (page != null) page.setPosition(sequence, position);
    }

    @SneakyThrows
    public void flush() {
        file.getChannel().force(true);
    }

    @SneakyThrows
    public void close() {
        file.close();
    }

    private IndexPage getPage(final long sequence) {
        return pages.get(getPageNumber(sequence));
    }

    @SneakyThrows
    private IndexPage loadPage(final long sequence) {
        flush();

        final var pageNumber = getPageNumber(sequence);
        final var startSequence = (pageNumber - 1) * PAGE_SIZE;
        final var endSequence = min(startSequence + PAGE_SIZE - 1, entryCount);

        // page
        final var page = new IndexPage(startSequence);
        file.seek(getIndexPosition(startSequence));
        for (var s = startSequence; s <= endSequence; s++) {
            page.setPosition(s, file.readLong());
        }

        // cache
        pages.put(pageNumber, page);
        return page;
    }

    private static long getIndexPosition(final long sequence) {
        return 8 + (sequence - 1) * 8;
    }

    private static int getPageNumber(final long sequence) {
        return 1 + (int) (sequence / PAGE_SIZE);
    }
}