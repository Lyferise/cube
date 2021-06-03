package com.lyferise.cube.node;

import com.lyferise.cube.node.wal.WalDispatcher;
import com.lyferise.cube.node.wal.WalEntry;
import com.lyferise.cube.protocol.MessageReader;
import lombok.extern.slf4j.Slf4j;

import static com.lyferise.cube.protocol.MessageCode.AUTH;

@Slf4j
public class Dispatcher implements WalDispatcher {

    @Override
    public void dispatch(final WalEntry entry) {
        final var reader = new MessageReader(entry.getData());
        log.info("dispatch {}", reader.getMessageCode());

        switch (reader.getMessageCode()) {
            case AUTH -> log.info("AUTH {}", reader.readString());
        }
    }
}