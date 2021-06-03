package com.lyferise.cube.node;

import com.lyferise.cube.node.messages.Message;
import com.lyferise.cube.node.messages.MessageProcessor;
import com.lyferise.cube.node.wal.WalDispatcher;
import com.lyferise.cube.node.wal.WalEntry;
import com.lyferise.cube.node.websockets.SessionManager;
import com.lyferise.cube.protocol.MessageReader;
import lombok.extern.slf4j.Slf4j;

import static com.lyferise.cube.protocol.MessageCode.AUTH;

@Slf4j
public class Dispatcher implements WalDispatcher {
    private final SessionManager sessionManager;
    private final MessageProcessor authenticator;

    public Dispatcher(final SessionManager sessionManager, final MessageProcessor authenticator) {
        this.sessionManager = sessionManager;
        this.authenticator = authenticator;
    }

    @Override
    public void dispatch(final WalEntry entry) {
        final var reader = new MessageReader(entry.getData());
        final var message = new Message(entry.getSpacetimeId(), entry.getSessionKey(), reader);
        switch (message.getMessageCode()) {
            case AUTH -> authenticator.process(message);
            default -> log.info("dispatch {}", reader.getMessageCode());
        }
    }
}