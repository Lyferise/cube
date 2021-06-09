package com.lyferise.cube.events;

public class SequenceNumberException extends RuntimeException {

    public SequenceNumberException(final long sequenceNumber, final long expectedSequenceNumber) {
        super("Sequence number check failed: expected "
                + expectedSequenceNumber
                + " not "
                + sequenceNumber
                + ".");
    }
}