package com.lyferise.cube.node.security;

import com.lyferise.cube.node.messages.Message;
import com.lyferise.cube.node.messages.MessageProcessor;
import com.lyferise.cube.node.messages.MessagePublisher;
import com.lyferise.cube.serialization.ByteArrayWriter;
import lombok.extern.slf4j.Slf4j;

import static com.lyferise.cube.protocol.MessageCode.AUTH_SUCCESS;

@Slf4j
public class DefaultAuthenticator implements MessageProcessor {
    private final MessagePublisher messagePublisher;

    public DefaultAuthenticator(final MessagePublisher messagePublisher) {
        this.messagePublisher = messagePublisher;
    }

    @Override
    public void process(final Message message) {

        // credentials
        final var reader = message.getReader();
        final String username = reader.readString();
        final String password = reader.readString();

        // auth
        if (authenticate(username, password)) {
            final var sessionKey = message.getSessionKey();
            log.info("AUTH {}", sessionKey);
            var writer = new ByteArrayWriter();
            writer.writeShort(AUTH_SUCCESS);
            messagePublisher.send(sessionKey, writer.toByteArray());
        }
    }

    private boolean authenticate(final String username, final String password) {
        return username.equals("test") && password.equals("test");
    }
}