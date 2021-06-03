package com.lyferise.cube.node.messages;

import com.lyferise.cube.events.SpacetimeId;
import com.lyferise.cube.protocol.MessageReader;
import lombok.Value;

import java.util.UUID;

@Value
public class Message {
    SpacetimeId spacetimeId;
    UUID sessionKey;
    MessageReader reader;

    public int getMessageCode() {
        return reader.getMessageCode();
    }
}