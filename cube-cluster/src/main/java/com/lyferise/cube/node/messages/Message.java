package com.lyferise.cube.node.messages;

import com.lyferise.cube.events.SpacetimeId;
import com.lyferise.cube.serialization.BinaryReader;
import lombok.Value;

import java.util.UUID;

@Value
public class Message {
    SpacetimeId spacetimeId;
    UUID sessionKey;
    BinaryReader reader;
}