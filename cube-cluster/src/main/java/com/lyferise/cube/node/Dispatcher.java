package com.lyferise.cube.node;

import com.lyferise.cube.node.messages.Message;
import com.lyferise.cube.node.messages.MessageProcessor;
import com.lyferise.cube.node.websockets.SessionManager;
import lombok.extern.slf4j.Slf4j;

import static com.lyferise.cube.protocol.MessageCode.AUTH;

@Slf4j
public class Dispatcher {
    private final SessionManager sessionManager;
    private final MessageProcessor authenticator;

    public Dispatcher(final SessionManager sessionManager, final MessageProcessor authenticator) {
        this.sessionManager = sessionManager;
        this.authenticator = authenticator;
    }

    public void dispatch(final Message message) {
        final var messageCode = message.getReader().readShort();
        switch (messageCode) {
            case AUTH -> authenticator.process(message);
            default -> log.info("dispatch {}", messageCode);
        }
    }
}