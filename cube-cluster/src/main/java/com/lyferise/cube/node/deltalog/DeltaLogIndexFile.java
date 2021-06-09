package com.lyferise.cube.node.deltalog;

import com.lyferise.cube.node.wal.IndexPage;
import lombok.SneakyThrows;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.lyferise.cube.events.SequenceNumber.verifySequenceNumber;
import static com.lyferise.cube.node.wal.IndexPage.PAGE_SIZE;
import static java.lang.Math.min;

public class DeltaLogIndexFile {
    private static final long MAX_PAGE_COUNT = 1000;
    private long recordCount;
    private final RandomAccessFile file;

    private final Map<Integer, IndexPage> pages = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(final Map.Entry<Integer, IndexPage> eldest) {
            return size() > MAX_PAGE_COUNT;
        }
    };

    @SneakyThrows
    public DeltaLogIndexFile(final String path) {

        // file
        final var f = new File(path);
        final var newFile = f.createNewFile();
        file = new RandomAccessFile(f, "rw");

        // header
        if (newFile) {
            file.writeLong(0);
        } else {
            recordCount = file.readLong();
        }
    }

    @SneakyThrows
    public long getPosition(final long logSequenceNumber) {
        var page = getPage(logSequenceNumber);
        if (page == null) page = loadPage(logSequenceNumber);
        return page.getPosition(logSequenceNumber);
    }

    @SneakyThrows
    public void setPosition(final long logSequenceNumber, final long position) {

        // sequence
        verifySequenceNumber(logSequenceNumber, recordCount + 1);

        // index
        file.seek(getIndexPosition(logSequenceNumber));
        file.writeLong(position);

        // header
        file.seek(0);
        file.writeLong(++recordCount);

        // page
        final var page = getPage(logSequenceNumber);
        if (page != null) page.setPosition(logSequenceNumber, position);
    }

    @SneakyThrows
    public void flush() {
        file.getChannel().force(true);
    }

    @SneakyThrows
    public void close() {
        file.close();
    }

    private IndexPage getPage(final long logSequenceNumber) {
        return pages.get(getPageNumber(logSequenceNumber));
    }

    @SneakyThrows
    private IndexPage loadPage(final long logSequenceNumber) {
        flush();

        final var pageNumber = getPageNumber(logSequenceNumber);
        final var logSequenceNumberStart = (pageNumber - 1) * PAGE_SIZE;
        final var logSequenceNumberEnd = min(logSequenceNumberStart + PAGE_SIZE - 1, recordCount);

        // page
        final var page = new IndexPage(logSequenceNumberStart);
        file.seek(getIndexPosition(logSequenceNumberStart));
        for (var s = logSequenceNumberStart; s <= logSequenceNumberEnd; s++) {
            page.setPosition(s, file.readLong());
        }

        // cache
        pages.put(pageNumber, page);
        return page;
    }

    private static long getIndexPosition(final long logSequenceNumber) {
        return 8 + (logSequenceNumber - 1) * 8;
    }

    private static int getPageNumber(final long logSequenceNumber) {
        return 1 + (int) (logSequenceNumber / PAGE_SIZE);
    }
}