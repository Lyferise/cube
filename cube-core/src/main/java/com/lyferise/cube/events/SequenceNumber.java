package com.lyferise.cube.events;

public class SequenceNumber {

    public static void verifySequenceNumber(final long sequenceNumber, final long expectedSequenceNumber) {
        if (sequenceNumber != expectedSequenceNumber) {
            throw new SequenceNumberException(sequenceNumber, expectedSequenceNumber);
        }
    }
}