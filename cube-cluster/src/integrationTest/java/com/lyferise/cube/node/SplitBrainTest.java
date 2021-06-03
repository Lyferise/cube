package com.lyferise.cube.node;

/*
Integration test: Should merge two channels after split brain

A key lesson from fundamental physics is that in the universe, information is local. This test emphasizes that data is
generated locally but needs to be synchronized universally (through data exchange between nodes). Unfortunately, data
delivery is never guaranteed, and data may arrive in an uncertain order.

TL;DR - If the connection between two data centres breaks and comes back up, and two members were trying to message to
each other at the same time, the system won't get confused and create two new conversations that contain both people
seeing different messages.

In the test scenario, two members located in different parts of the world (say Alice and Bob) are connected to
different chat servers (nodes), for example Frankfurt and Singapore. The connection breaks between the two data
centres, stopping universal data exchange. Alice and Bob try to message each other. Locally, the data centres
independently set up a unique chat channel that contains both members (although resulting in different channel IDs)
to hold the messages. Until replication can continue, the two channels will have a distinct message history. Once the
connection between data centres is restored, data is exchanged, and the two channels are merged into a single channel
with all messages visible to both Alice and Bob.

1. Set up a cluster with two nodes
2. Create two members (Alice and Bob) and wait for replication
3. Verify that both nodes have a full replica of the member list (so both Alice and Bob exist as members in both nodes)
4. Break the connection between the two nodes
5. Set up two clients (Alice -> node 1, Bob -> node 2)
6. Alice sets up a channel to talk to Bob and sends message A once the channel is created
7. Bob sets up a channel to talk to Alice and sends message B once the channel is created
8. Alice sees only message A in chat history
9. Bob sees only message B in chat history
10. We restore the connection between the two nodes
11. The two chat channels merge as part of a replicated CRDT merge operation
12. Both Alice and Bob see messages A and B in their local chat histories
 */

public class SplitBrainTest {
}