package com.lyferise.cube.node.deltalog;

public class DeltaLogIndexPage {
    public static int PAGE_SIZE = 1024;

    private final long[] positions = new long[PAGE_SIZE];
    private final long logSequenceNumber;

    public DeltaLogIndexPage(final long logSequenceNumber) {
        this.logSequenceNumber = logSequenceNumber;
    }

    public long getPosition(final long logSequenceNumber) {
        return positions[(int) (logSequenceNumber - this.logSequenceNumber)];
    }

    public void setPosition(final long logSequenceNumber, final long position) {
        positions[(int) (logSequenceNumber - this.logSequenceNumber)] = position;
    }
}