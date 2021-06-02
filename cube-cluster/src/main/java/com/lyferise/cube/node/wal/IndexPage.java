package com.lyferise.cube.node.wal;

public class IndexPage {
    public static int PAGE_SIZE = 1024;

    private final long[] positions = new long[PAGE_SIZE];
    private long sequence;

    public IndexPage(final long sequence) {
        this.sequence = sequence;
    }

    public long getPosition(final long sequence) {
        return positions[(int) (sequence - this.sequence)];
    }

    public void setPosition(final long sequence, final long position) {
        positions[(int) (sequence - this.sequence)] = position;
    }
}