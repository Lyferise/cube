package com.lyferise.cube.node.websockets;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum ServerState {
    CREATED,
    STARTED,
    FAILED_TO_START,
    STOPPED
}