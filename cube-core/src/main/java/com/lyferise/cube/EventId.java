package com.lyferise.cube;

/*
Inspired by the physics of spacetime, Cube uses 128-bit unique identifiers, known as Event IDs. When Cube needs to
uniquely label a new entity, it generates an ID corresponding to that entity's creation event. For example, the unique
ID for a chat channel depends on where and when that channel was created.

Generally, in computing and data processing, it is often desirable to associate a unique identifier to each entity or
data item of interest, such as people, places or addresses. In distributed computing, unique identifiers can either be
generated locally or via coordination with other nodes on the network. For scalability and to avoid coordination, Cube
uses locally generated IDs that are guaranteed to be sortable in time and decentralized in space.

In human-readable display form, an Event ID (e.g. a member ID or a chat message ID) is represented as three numbers
formatted as 'a.b@T'. This indicates the location and time of the creation event globally, for example:

    4.789128@96889928348

The first two numbers (4.789128) locate the creation event physically in space:
    node = 4 // a unique machine ID assigned to each datacenter node
    sequence = 789128 // a sequence number locally unique to each node

The last number locates the creation event in time:
    time = 96889928348 // milliseconds since epoch (1st January 2020)

For efficiency (e.g. as a database primary key), an Event ID may also be encoded as a pair of 64-bit numbers:
    space // 20-bit node ID with 44-bit sequence number
    time // ms since epoch

This encoding scheme supports 2^20 nodes (> 1 million nodes), covers a time span of 2^64 milliseconds (> 584 million
years) and allows for 2^44 unique messages per node (> 17 trillion messages per node).
 */

import static java.lang.String.valueOf;

public class EventId {
    private final long space;
    private final long time;

    public EventId(final long space, final long time) {
        this.space = space;
        this.time = time;
    }

    public EventId(final long node, final long sequence, final long time) {
        this((node << 44) | sequence, time);
    }

    public long getSpace() {
        return space;
    }

    public long getTime() {
        return time;
    }

    public long getNode() {
        return space >> 44;
    }

    public long getSequence() {
        return space & 0xFFFFFFFFFFFL;
    }

    @Override
    public String toString() {
        return valueOf(getNode()) + '.' + getSequence() + '@' + time;
    }
}