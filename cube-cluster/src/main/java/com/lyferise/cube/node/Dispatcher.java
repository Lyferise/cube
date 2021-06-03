package com.lyferise.cube.node;

import com.lyferise.cube.node.wal.WalDispatcher;
import com.lyferise.cube.node.wal.WalEntry;
import com.lyferise.cube.protocol.MessageReader;
import com.lyferise.cube.protocol.MessageWriter;
import lombok.extern.slf4j.Slf4j;

import static com.lyferise.cube.protocol.MessageCode.AUTH;
import static com.lyferise.cube.protocol.MessageCode.AUTH_SUCCESS;

@Slf4j
public class Dispatcher implements WalDispatcher {
    private final MessagePublisher messagePublisher;

    public Dispatcher(final MessagePublisher messagePublisher) {
        this.messagePublisher = messagePublisher;
    }

    @Override
    public void dispatch(final WalEntry entry) {
        final var reader = new MessageReader(entry.getData());
        switch (reader.getMessageCode()) {
            case AUTH -> {
                log.info("AUTH {}", entry.getSessionKey());
                var writer = new MessageWriter(AUTH_SUCCESS);
                messagePublisher.send(entry.getSessionKey(), writer.toByteArray());
            }
            default -> log.info("dispatch {}", reader.getMessageCode());
        }
    }
}